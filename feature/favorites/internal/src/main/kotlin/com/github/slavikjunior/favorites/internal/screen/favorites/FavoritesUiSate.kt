package com.github.slavikjunior.favorites.internal.screen.favorites

import androidx.compose.runtime.Stable
import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Price

@Stable
internal data class FavoritesUiSate(
    val productsMap: Map<CategoryType, List<Product>> = emptyMap(),
    val status: FavoritesUiStateStatus = FavoritesUiStateStatus.Loading,
    val selectedCategoryType: CategoryType = CategoryType.COFFEE,
    val selectedProduct: ProductWithModifiers? = null,
    val favouriteProductIds: Set<ID> = emptySet(),
    val cartPrice: Price = Price(0, 0),
)

internal sealed interface FavoritesUiStateStatus {

    data object Loading : FavoritesUiStateStatus

    data object Success : FavoritesUiStateStatus

    data class Error(val cause: Throwable) : FavoritesUiStateStatus
}