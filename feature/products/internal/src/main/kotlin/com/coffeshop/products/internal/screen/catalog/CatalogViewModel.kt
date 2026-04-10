package com.coffeshop.products.internal.screen.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.result.Result
import com.coffeshop.products.api.domain.usecase.GetFullMenuUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KClass

internal sealed interface CatalogUiState {
    data object Loading : CatalogUiState
    data class Success(
        val categories: List<String>,
        val selectedIndex: Int,
        val products: List<ProductUiItem>,
    ) : CatalogUiState
    data class Error(val message: String) : CatalogUiState
}

internal data class ProductUiItem(
    val id: String,
    val name: String,
    val price: String,
    val imageUrl: String?,
)

internal class CatalogViewModel
@Inject constructor(
    private val getFullMenu: GetFullMenuUseCase,
) : ViewModel() {

    private var allProducts: Map<String, List<ProductUiItem>> = emptyMap()

    private val _uiState = MutableStateFlow<CatalogUiState>(CatalogUiState.Loading)
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    init {
        loadMenu()
    }

    fun onCategorySelected(index: Int) {
        val state = _uiState.value as? CatalogUiState.Success ?: return
        val categoryKey = state.categories.getOrNull(index) ?: return
        _uiState.update {
            state.copy(
                selectedIndex = index,
                products = allProducts[categoryKey] ?: emptyList(),
            )
        }
    }

    private fun loadMenu() {
        viewModelScope.launch {
            _uiState.update { CatalogUiState.Loading }
            when (val result = getFullMenu()) {
                is Result.Success -> onMenuLoaded(result.data)
                is Result.Error -> _uiState.update {
                    CatalogUiState.Error(result.exception.message ?: "Ошибка загрузки меню")
                }
                Result.Loading -> Unit
            }
        }
    }

    private fun onMenuLoaded(products: List<Product>) {
        allProducts = products
            .groupBy { it.category.categoryType.displayName() }
            .mapValues { (_, items) -> items.map { it.toUiItem() } }

        val categories = allProducts.keys.toList()
        _uiState.update {
            CatalogUiState.Success(
                categories = categories,
                selectedIndex = 0,
                products = allProducts[categories.firstOrNull() ?: ""] ?: emptyList(),
            )
        }
    }

    companion object {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
                return CatalogViewModel(
                    getFullMenu = object : GetFullMenuUseCase {
                        override suspend fun invoke() = Result.Success(emptyList<Product>())
                    }
                ) as T
            }
        }
    }
}

private fun Product.toUiItem() = ProductUiItem(
    id = productId.value,
    name = productName.value,
    price = prices.values.minByOrNull { it.firstPart }?.formatPrice() ?: "",
    imageUrl = imageUrl,
)

private fun Price.formatPrice(): String = "от $firstPart ₽"

private fun CategoryType.displayName(): String = when (this) {
    CategoryType.COFFEE -> "Кофе"
    CategoryType.MATCHA -> "Матча"
    CategoryType.NON_COFFEE -> "Не кофе"
    CategoryType.SIGNATURE -> "Фирменные"
}
