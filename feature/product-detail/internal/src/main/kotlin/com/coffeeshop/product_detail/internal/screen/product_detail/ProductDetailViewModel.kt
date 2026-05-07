package com.coffeeshop.product_detail.internal.screen.product_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arttttt.nav3router.Router
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
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.result.asErrorResult
import com.coffeeshop.common.result.isSuccess
import com.coffeeshop.di.qualifiers.DispatcherDefault
import com.coffeeshop.product_detail.api.domain.usecase.CalculateProductTotalPriceUseCase
import com.coffeeshop.product_detail.api.domain.usecase.DecrementQuantityUseCase
import com.coffeeshop.product_detail.api.domain.usecase.IncrementQuantityUseCase
import com.coffeeshop.utils.findOrThrow
import com.coffeshop.navigation.Route
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2

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
    val selectedVolume: Size = Size.MEDIUM,
    val selectedModifiers: Map<ModifierCategory, Modifier> = emptyMap(),

    val quantity: Int = 0,
    val comment: String = "",
    val totalPrice: String = "",
) {
    val name: String = selectedProduct?.productName?.value.orEmpty()
    val imageUrl: String? = selectedProduct?.imageUrl
    val volumes = selectedProduct?.availableSizes?.sortedBy { it.ml }?.map { it.display() } ?: emptyList()
    val modifierGroups = selectedProduct?.compatibleModifiers
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
        size = selectedVolume,
        quantity = quantity,
        modifiers = selectedModifiers.values.toList()
    )
}

internal sealed interface ProductDetailUiStateEvent {

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
    private val calculateProductTotalPrice: CalculateProductTotalPriceUseCase,
    private val incrementQuantity: IncrementQuantityUseCase,
    private val decrementQuantity: DecrementQuantityUseCase,
    @param:DispatcherDefault private val dispatcher: CoroutineDispatcher,
    private val router: Router<Route>
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun reduce(event: ProductDetailUiStateEvent) {
        when (event) {
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

    private fun onAddToCart() {
        // handle with logic from :feature:cart:api
    }

    private fun onCommentChanged(event: ProductDetailUiStateEvent.CommentChanged) {
        _uiState.update { it.copy(comment = event.comment) }
    }

    private fun onIncrementQuantity(event: ProductDetailUiStateEvent.IncrementQuantity) {
        viewModelScope.launch(dispatcher) {
            val newQuantity = async { incrementQuantity(event.current) }.await()

            if (newQuantity.isSuccess()) {
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            quantity = (newQuantity as Result.Success<Int>).data
                        )
                    }
                }
            }
        }
    }

    private fun onDecrementQuantity(event: ProductDetailUiStateEvent.DecrementQuantity) {
        viewModelScope.launch(dispatcher) {
            val newQuantity = async { decrementQuantity(event.current) }.await()

            if (newQuantity.isSuccess()) {
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            quantity = (newQuantity as Result.Success<Int>).data
                        )
                    }
                }
            }
        }
    }

    private fun onSelectModifier(event: ProductDetailUiStateEvent.SelectModifier) {
        val modifier: Modifier = _uiState.value.selectedProduct?.compatibleModifiers?.findOrThrow { it.additiveName.value == event.optionTitle } ?: throw IllegalArgumentException("Invalid modifier")

        _uiState.update { state ->
            val current: Modifier? = state.selectedModifiers[modifier.category]
            val newModifiers: Map<ModifierCategory, Modifier> = if (current?.additiveId == modifier.additiveId) {
                state.selectedModifiers - modifier.category
            } else {
                state.selectedModifiers + (modifier.category to modifier)
            }
            state.copy(selectedModifiers = newModifiers)
        }
    }

    private fun onSelectVolume(event: ProductDetailUiStateEvent.SelectVolume) {
        val size = Size.entries.findOrThrow(message = "Incorrect size") { it.display() == event.volumeString }
        _uiState.update { it.copy(selectedVolume = size) }
    }

    private fun onDismissProductDetailBottomSheet() {
        router.pop()
    }

    private fun onCalculateProductTotalPrice() {
        _uiState.update {
            it.copy(status = ProductDetailUiStateStatus.Loading)
        }

        var result: Result<Price>? = null
        viewModelScope.launch {
             result = try {
                calculateProductTotalPrice(
                    orderItem = _uiState.value.toOrderItem()
                )
            } catch (cause: Throwable) {
                cause.asErrorResult()
            }
        }

        if (result.isSuccess()) {
            _uiState.update {
                it.copy(
                    totalPrice = (result as Result.Success<Price>).data.display()
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    status = ProductDetailUiStateStatus.Error
                )
            }
        }
    }

    override fun onCleared() {
        viewModelScope.cancel("$TAG onCleared")

        super.onCleared()
    }

    private companion object {
        const val TAG = "ProductDetailViewModel"
    }
}