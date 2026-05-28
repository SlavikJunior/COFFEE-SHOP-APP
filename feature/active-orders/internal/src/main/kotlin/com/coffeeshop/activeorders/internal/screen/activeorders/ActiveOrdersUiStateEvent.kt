package com.coffeeshop.activeorders.internal.screen.activeorders

internal sealed interface ActiveOrdersUiStateEvent {
    data object Retry : ActiveOrdersUiStateEvent
    data object NavigateBack : ActiveOrdersUiStateEvent
    data object BottomNavigateToCatalog : ActiveOrdersUiStateEvent
    data object BottomNavigateToFavorites : ActiveOrdersUiStateEvent
    data object BottomNavigateToProfile : ActiveOrdersUiStateEvent
}