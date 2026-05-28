package com.github.slavikjunior.favorites.internal.screen.favorites

import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.common.model.support.ID

interface FavoritesUiStateEvent {
    data object RetryAfterErrorClicked : FavoritesUiStateEvent
    data class ChangeCategoryType(val categoryType: CategoryType) : FavoritesUiStateEvent
    data class ToggleProductFavorite(val productId: ID) : FavoritesUiStateEvent
    data class GetProductDetail(val productId: ID) : FavoritesUiStateEvent
    data object ProfileClicked : FavoritesUiStateEvent
    data object NavigateToCart : FavoritesUiStateEvent
    data object BottomNavigateToCatalog : FavoritesUiStateEvent
    data object BottomNavigateToProfile : FavoritesUiStateEvent
    data object BottomNavigateToActiveOrders : FavoritesUiStateEvent
}