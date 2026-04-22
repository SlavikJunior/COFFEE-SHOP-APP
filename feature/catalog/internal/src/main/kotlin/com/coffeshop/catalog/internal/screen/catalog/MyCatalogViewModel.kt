package com.coffeshop.catalog.internal.screen.catalog

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.result.Result
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
}

@Stable
internal data class MyCatalogModel(
    val products: List<Product> = emptyList(),
    val state: MyCatalogUiState = MyCatalogUiState.Loading,
    val selectedCategoryType: CategoryType = CategoryType.COFFEE
)

internal sealed interface MyCatalogEvent {

    data object RetryAfterErrorClicked : MyCatalogEvent

    data object LoadProductsForCurrentCategoryType : MyCatalogEvent

    data class ChangeCategoryType(val categoryType: CategoryType) : MyCatalogEvent

    data class ToggleProductFavorite(val productId: ID) : MyCatalogEvent

    data class GetProductDetail(val productId: ID) : MyCatalogEvent
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
            MyCatalogEvent.LoadProductsForCurrentCategoryType -> TODO()
            is MyCatalogEvent.ChangeCategoryType -> TODO()
            is MyCatalogEvent.GetProductDetail -> TODO()
            is MyCatalogEvent.ToggleProductFavorite -> TODO()
        }
    }

    private fun onRetryAfterErrorClicked() {
        _uiState.update {
            it.copy(
                state = MyCatalogUiState.Loading
            )
        }

        initData()
    }

    private fun onLoadProductsForCurrentCategoryType() {
        _uiState.update {
            it.copy(
                products = productsIntoMap?.getOrElse(
                    key = _uiState.value.selectedCategoryType
                ) { emptyList() } ?: emptyList()
            )
        }
    }

    private fun onChangeCategoryType(event: MyCatalogEvent.ChangeCategoryType) {
        _uiState.update {
            it.copy(
                selectedCategoryType = event.categoryType
            )
        }
    }

    private fun initData() {
        searchJob = viewModelScope.launch {
            when (val result = getFullMenu()) {
                Result.Loading -> {
                    _uiState.update {
                        it.copy(
                            state = MyCatalogUiState.Loading
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            state = MyCatalogUiState.Error(cause = result.exception)
                        )
                    }
                }

                is Result.Success<*> -> {
                    _uiState.update {
                        it.copy(
                            state = MyCatalogUiState.Success
                        )
                    }

                    result as Result.Success<List<Product>>
                    products = result.data

                    products?.let { products ->
                        productsIntoMap = products.groupBy<CategoryType, Product>()
                    }
                    products = null
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