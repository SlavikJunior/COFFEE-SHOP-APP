package com.coffeeshop.product_detail.internal.screen.product_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arttttt.nav3router.Router
import com.coffeeshop.cart.api.domain.model.CartItem
import com.coffeeshop.cart.api.domain.model.CartItemModifier
import com.coffeeshop.cart.api.domain.usecase.AddToCartUseCase
import com.coffeeshop.common.model.order.OrderItem
import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.common.model.products.Modifier
import com.coffeeshop.common.model.products.ModifierCategory
import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.products.display
import com.coffeeshop.common.model.products.toProduct
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.model.support.Size
import com.coffeeshop.common.model.support.display
import com.coffeeshop.common.model.support.toPrice
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.result.asErrorResult
import com.coffeeshop.common.result.isSuccess
import com.coffeeshop.di.qualifiers.DispatcherDefault
import com.coffeeshop.di.qualifiers.DispatcherMain
import com.coffeeshop.product_detail.api.domain.usecase.CalculateProductTotalPriceUseCase
import com.coffeeshop.product_detail.api.domain.usecase.DecrementQuantityUseCase
import com.coffeeshop.product_detail.api.domain.usecase.IncrementQuantityUseCase
import com.coffeeshop.utils.findOrThrow
import com.coffeshop.catalog.api.domain.usecase.GetProductDetailFromCacheUseCase
import com.coffeshop.catalog.api.domain.usecase.RemoveProductDetailFromCacheUseCase
import com.coffeshop.navigation.Route
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal sealed interface ProductDetailUiStateStatus {

    data object Loading : ProductDetailUiStateStatus
    data object Success : ProductDetailUiStateStatus
    data object Error : ProductDetailUiStateStatus
}

internal data class ModifierGroup(
    val title: String,
    val options: List<String>,
    val selectedOption: String?,
)

internal data class ProductDetailUiState(
    val status: ProductDetailUiStateStatus = ProductDetailUiStateStatus.Loading,

    val selectedProduct: ProductWithModifiers? = null,
    val selectedCategoryType: CategoryType = CategoryType.COFFEE,
    val selectedVolume: Size? = null,
    val selectedModifiers: Map<ModifierCategory, Modifier> = emptyMap(),

    val quantity: Int = 1,
    val comment: String = "",
    val totalPrice: String = "",
) {
    val volumes: List<String> = selectedProduct?.availableSizes?.sortedBy { it.ml }?.map { it.display() } ?: emptyList()
    val modifierGroups: List<ModifierGroup> = selectedProduct?.compatibleModifiers
        ?.groupBy { it.category }
        ?.map { (category, modifiers) ->
            ModifierGroup(
                title = category.display(),
                options = modifiers.map { it.additiveName.value },
                selectedOption = selectedModifiers[category]?.additiveName?.value,
            )
        } ?: emptyList()
}

internal fun ProductDetailUiState.toOrderItem(): OrderItem {
    return OrderItem(
        orderItemId = ID.random(),
        product = selectedProduct?.toProduct() ?: throw IllegalStateException("Selected product cannot be null"),
        size = selectedVolume ?: throw IllegalStateException("Selected volume cannot be null"),
        quantity = quantity,
        modifiers = selectedModifiers.values.toList()
    )
}

internal sealed interface ProductDetailUiStateEvent {

    data class LoadProduct(val productId: ID) : ProductDetailUiStateEvent
    data object CalculateProductTotalPrice : ProductDetailUiStateEvent
    data object DismissProductDetailBottomSheet : ProductDetailUiStateEvent
    data class SelectVolume(val volumeString: String) : ProductDetailUiStateEvent
    data class SelectModifier(val groupTitle: String, val optionTitle: String) : ProductDetailUiStateEvent
    data class IncrementQuantity(val current: Int) : ProductDetailUiStateEvent
    data class DecrementQuantity(val current: Int) : ProductDetailUiStateEvent
    data class CommentChanged(val comment: String) : ProductDetailUiStateEvent
    data object AddToCart : ProductDetailUiStateEvent
}

internal class ProductDetailViewModel
@Inject constructor(
    private val addToCart: AddToCartUseCase,
    private val calculateProductTotalPrice: CalculateProductTotalPriceUseCase,
    private val incrementQuantity: IncrementQuantityUseCase,
    private val decrementQuantity: DecrementQuantityUseCase,
    private val getProductDetailFromCache: GetProductDetailFromCacheUseCase,
    private val removeProductDetailFromCache: RemoveProductDetailFromCacheUseCase,
    @param:DispatcherDefault private val defaultDispatcher: CoroutineDispatcher,
    @param:DispatcherMain private val mainDispatcher: CoroutineDispatcher,
    private val router: Router<Route>
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun reduce(event: ProductDetailUiStateEvent) {
        when (event) {
            is ProductDetailUiStateEvent.LoadProduct -> onLoadProduct(event)
            ProductDetailUiStateEvent.CalculateProductTotalPrice -> onCalculateProductTotalPrice()
            ProductDetailUiStateEvent.DismissProductDetailBottomSheet -> onDismissProductDetailBottomSheet()
            is ProductDetailUiStateEvent.SelectVolume -> onSelectVolume(event)
            is ProductDetailUiStateEvent.SelectModifier -> onSelectModifier(event)
            is ProductDetailUiStateEvent.DecrementQuantity -> onDecrementQuantity(event)
            is ProductDetailUiStateEvent.IncrementQuantity -> onIncrementQuantity(event)
            is ProductDetailUiStateEvent.CommentChanged -> onCommentChanged(event)
            ProductDetailUiStateEvent.AddToCart -> onAddToCart()
        }
    }

    private fun onLoadProduct(event: ProductDetailUiStateEvent.LoadProduct) {
        Log.d(TAG, "onLoadProduct invoked with productId: ${event.productId.value}")

        viewModelScope.launch {
            when (val result = getProductDetailFromCache(event.productId)) {
                is Result.Success -> {
                    Log.d(TAG, "SuccessResult on get product detail from cache: ${result.data}")

                    val product = result.data
                    _uiState.update {
                        it.copy(
                            selectedProduct = product,
                            selectedVolume = product.availableSizes.minByOrNull { s -> s.ml } ?: Size.MEDIUM,
                        )
                    }
                    onCalculateProductTotalPrice()
                }
                is Result.Error -> _uiState.update { it.copy(status = ProductDetailUiStateStatus.Error) }
                else -> _uiState.update { it.copy(status = ProductDetailUiStateStatus.Error) }
            }
        }
    }

    private fun onAddToCart() {
        val state = _uiState.value
        val selectedProduct = state.selectedProduct ?: return
        val price = state.totalPrice.toPrice() ?: return

        val cartItem = CartItem(
            productId = selectedProduct.productId,
            productName = selectedProduct.productName,
            imageUrl = selectedProduct.imageUrl,
            price = price,
            size = state.selectedVolume ?: Size.MEDIUM,
            quantity = state.quantity,
            comment = state.comment,
            selectedModifiers = state.selectedModifiers.values.map { modifier ->
                CartItemModifier(
                    id = modifier.additiveId,
                    name = modifier.additiveName.value,
                    price = modifier.price,
                    category = modifier.category,
                )
            },
        )

        CoroutineScope(SupervisorJob() + defaultDispatcher).launch {
            addToCart(cartItem)
        }

        onDismissProductDetailBottomSheet()
    }

    private fun onCommentChanged(event: ProductDetailUiStateEvent.CommentChanged) {
        _uiState.update { it.copy(comment = event.comment) }
    }

    private fun onIncrementQuantity(event: ProductDetailUiStateEvent.IncrementQuantity) {
        viewModelScope.launch(defaultDispatcher) {
            val newQuantity = async { incrementQuantity(event.current) }.await()

            if (newQuantity.isSuccess()) {
                withContext(mainDispatcher) {
                    _uiState.update {
                        it.copy(quantity = (newQuantity as Result.Success<Int>).data)
                    }
                }

                onCalculateProductTotalPrice()
            }
        }
    }

    private fun onDecrementQuantity(event: ProductDetailUiStateEvent.DecrementQuantity) {
        viewModelScope.launch(defaultDispatcher) {
            val newQuantity = async { decrementQuantity(event.current) }.await()

            if (newQuantity.isSuccess()) {
                withContext(mainDispatcher) {
                    _uiState.update {
                        it.copy(quantity = (newQuantity as Result.Success<Int>).data)
                    }
                }
            }

            onCalculateProductTotalPrice()
        }
    }

    private fun onSelectModifier(event: ProductDetailUiStateEvent.SelectModifier) {
        val modifier: Modifier = _uiState.value.selectedProduct?.compatibleModifiers
            ?.findOrThrow { it.additiveName.value == event.optionTitle }
            ?: throw IllegalArgumentException("Invalid modifier")

        _uiState.update { state ->
            val current: Modifier? = state.selectedModifiers[modifier.category]
            val newModifiers: Map<ModifierCategory, Modifier> = if (current?.additiveId == modifier.additiveId) {
                state.selectedModifiers - modifier.category
            } else {
                state.selectedModifiers + (modifier.category to modifier)
            }
            state.copy(selectedModifiers = newModifiers)
        }

        onCalculateProductTotalPrice()
    }

    private fun onSelectVolume(event: ProductDetailUiStateEvent.SelectVolume) {
        val size = Size.entries.findOrThrow(message = "Incorrect size") { it.display() == event.volumeString }
        _uiState.update { it.copy(selectedVolume = size) }

        onCalculateProductTotalPrice()
    }

    private fun onDismissProductDetailBottomSheet() {
        _uiState.value.selectedProduct?.productId?.let { id ->
            viewModelScope.launch {
                removeProductDetailFromCache(id)
            }
        }

        _uiState.update { ProductDetailUiState() }

        router.pop()
    }

    private fun onCalculateProductTotalPrice() {
        viewModelScope.launch {
            val result = try {
                calculateProductTotalPrice(orderItem = _uiState.value.toOrderItem())
            } catch (cause: Throwable) {
                cause.asErrorResult()
            }

            withContext(mainDispatcher) {
                if (result.isSuccess()) {
                    _uiState.update { it.copy(
                        status = ProductDetailUiStateStatus.Success,
                        totalPrice = (result as Result.Success<Price>).data.display(),
                    )}
                } else {
                    _uiState.update { it.copy(status = ProductDetailUiStateStatus.Error) }
                }
            }
        }
    }

    override fun onCleared() {
        val productId = _uiState.value.selectedProduct?.productId
        viewModelScope.cancel("$TAG onCleared")

        if (productId != null) {
            CoroutineScope(SupervisorJob() + defaultDispatcher).launch {
                removeProductDetailFromCache(productId)
            }
        }

        super.onCleared()
    }

    private companion object {
        const val TAG = "ProductDetailViewModel"
    }
}