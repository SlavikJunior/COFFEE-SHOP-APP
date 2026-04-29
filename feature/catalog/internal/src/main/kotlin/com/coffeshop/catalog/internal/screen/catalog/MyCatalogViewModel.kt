package com.coffeshop.catalog.internal.screen.catalog

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.common.model.products.Modifier
import com.coffeeshop.common.model.products.ModifierCategory
import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Size
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.result.asErrorResult
import com.coffeeshop.utils.groupBy
import com.coffeshop.catalog.api.domain.usecase.GetFullMenuUseCase
import com.coffeshop.catalog.api.domain.usecase.GetProductDetailByProductIdUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

internal sealed interface MyCatalogUiState {

    data object Loading : MyCatalogUiState

    data object Success : MyCatalogUiState

    data class Error(val cause: Throwable) : MyCatalogUiState

    data class ShowingProductDetail(val product: ProductWithModifiers) : MyCatalogUiState
}

@Stable
internal data class MyCatalogModel(
    val products: List<Product> = emptyList(),
    val state: MyCatalogUiState = MyCatalogUiState.Loading,
    val selectedCategoryType: CategoryType = CategoryType.COFFEE,
    val selectedProduct: ProductWithModifiers? = null,
    val selectedVolume: Size? = null,
    val selectedModifiers: Map<ModifierCategory, Modifier> = emptyMap(),
    val quantity: Int = 1,
    val comment: String = "",
    val favouriteProductIds: Set<ID> = emptySet(),
)

internal sealed interface MyCatalogEvent {

    data object RetryAfterErrorClicked : MyCatalogEvent

    data object LoadProductsForCurrentCategoryType : MyCatalogEvent

    data class ChangeCategoryType(val categoryType: CategoryType) : MyCatalogEvent

    data class ToggleProductFavorite(val productId: ID) : MyCatalogEvent

    data class GetProductDetail(val productId: ID) : MyCatalogEvent

    data object DismissProductDetail : MyCatalogEvent

    data class SelectVolume(val size: Size) : MyCatalogEvent

    data class SelectModifier(val modifier: Modifier) : MyCatalogEvent

    data object IncrementQuantity : MyCatalogEvent

    data object DecrementQuantity : MyCatalogEvent

    data class CommentChanged(val comment: String) : MyCatalogEvent
}

internal class MyCatalogViewModel
@Inject constructor(
    private val getFullMenu: GetFullMenuUseCase,
    private val getProductDetailByProductId: GetProductDetailByProductIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyCatalogModel())
    val uiState = _uiState.asStateFlow()

    private var products: List<Product>? = null
    private var productsIntoMap: Map<CategoryType, List<Product>>? = null

    private var searchJob: Job? = null

    init {
        initData()
    }

    fun reduce(event: MyCatalogEvent) {
        when (event) {
            MyCatalogEvent.RetryAfterErrorClicked -> onRetryAfterErrorClicked()
            MyCatalogEvent.LoadProductsForCurrentCategoryType -> onLoadProductsForCurrentCategoryType()
            is MyCatalogEvent.ChangeCategoryType -> {
                onChangeCategoryType(event)
                onLoadProductsForCurrentCategoryType()
            }
            is MyCatalogEvent.GetProductDetail -> onGetProductDetail(event)
            is MyCatalogEvent.ToggleProductFavorite -> onToggleProductFavorite(event)
            MyCatalogEvent.DismissProductDetail -> onDismissProductDetail()
            is MyCatalogEvent.SelectVolume -> onSelectVolume(event)
            is MyCatalogEvent.SelectModifier -> onSelectModifier(event)
            MyCatalogEvent.IncrementQuantity -> onIncrementQuantity()
            MyCatalogEvent.DecrementQuantity -> onDecrementQuantity()
            is MyCatalogEvent.CommentChanged -> onCommentChanged(event)
        }
    }

    private fun onRetryAfterErrorClicked() {
        _uiState.update { it.copy(state = MyCatalogUiState.Loading) }
        initData()
    }

    private fun onLoadProductsForCurrentCategoryType() {
        _uiState.update { state ->
            state.copy(
                products = productsIntoMap?.getOrElse(state.selectedCategoryType) { emptyList() }
                    ?: emptyList()
            )
        }
    }

    private fun onChangeCategoryType(event: MyCatalogEvent.ChangeCategoryType) {
        _uiState.update { it.copy(selectedCategoryType = event.categoryType) }
    }

    private fun onGetProductDetail(event: MyCatalogEvent.GetProductDetail) {
        searchJob = viewModelScope.launch {
            val result: Result<ProductWithModifiers> =
                try {
                    getProductDetailByProductId(productId = event.productId)
                } catch (cause: Throwable) {
                    cause.asErrorResult()
                }
            when (result) {
                is Result.Success<ProductWithModifiers> -> {
                    val product = result.data
                    _uiState.update {
                        it.copy(
                            state = MyCatalogUiState.ShowingProductDetail(product),
                            selectedProduct = product,
                            selectedVolume = product.availableSizes.firstOrNull(),
                            selectedModifiers = emptyMap(),
                            quantity = 1,
                            comment = "",
                        )
                    }
                }
                is Result.Error -> _uiState.update { it.copy(state = MyCatalogUiState.Error(result.exception)) }
                else -> throw IllegalArgumentException("Illegal product detail result: $result")
            }
        }
    }

    private fun onDismissProductDetail() {
        _uiState.update {
            it.copy(
                state = MyCatalogUiState.Success,
                selectedProduct = null,
                selectedVolume = null,
                selectedModifiers = emptyMap(),
                quantity = 1,
                comment = "",
            )
        }
    }

    private fun onSelectVolume(event: MyCatalogEvent.SelectVolume) {
        _uiState.update { it.copy(selectedVolume = event.size) }
    }

    private fun onSelectModifier(event: MyCatalogEvent.SelectModifier) {
        _uiState.update { state ->
            val current = state.selectedModifiers[event.modifier.category]
            val newModifiers = if (current?.additiveId == event.modifier.additiveId) {
                state.selectedModifiers - event.modifier.category
            } else {
                state.selectedModifiers + (event.modifier.category to event.modifier)
            }
            state.copy(selectedModifiers = newModifiers)
        }
    }

    private fun onIncrementQuantity() {
        _uiState.update { it.copy(quantity = it.quantity + 1) }
    }

    private fun onDecrementQuantity() {
        _uiState.update { it.copy(quantity = maxOf(1, it.quantity - 1)) }
    }

    private fun onCommentChanged(event: MyCatalogEvent.CommentChanged) {
        _uiState.update { it.copy(comment = event.comment) }
    }

    private fun onToggleProductFavorite(event: MyCatalogEvent.ToggleProductFavorite) {
        _uiState.update { state ->
            val ids = state.favouriteProductIds
            state.copy(
                favouriteProductIds = if (event.productId in ids) ids - event.productId else ids + event.productId
            )
        }
    }

    private fun initData() {
        searchJob = viewModelScope.launch {
            when (val result = getFullMenu()) {
                Result.Loading -> {
                    _uiState.update { it.copy(state = MyCatalogUiState.Loading) }
                }

                is Result.Error -> {
                    _uiState.update { it.copy(state = MyCatalogUiState.Error(cause = result.exception)) }
                }

                is Result.Success<*> -> {
                    result as Result.Success<List<Product>>
                    products = result.data

                    products?.let { list ->
                        productsIntoMap = list.groupBy<CategoryType, Product>()
                    }
                    onLoadProductsForCurrentCategoryType()
                    products = null

                    _uiState.update { it.copy(state = MyCatalogUiState.Success) }
                }
            }
        }
    }

    override fun onCleared() {
        searchJob?.cancel(CancellationException("$TAG onCleared"))
        super.onCleared()
    }

    private companion object {
        const val TAG = "CatalogViewModel"
    }
}
