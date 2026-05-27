package com.coffeeshop.activeorders.internal.screen.activeorders

import com.coffeeshop.activeorders.api.domain.model.ActiveOrder

internal sealed interface ActiveOrdersUiState {
    data object Loading : ActiveOrdersUiState
    data class Error(val message: String) : ActiveOrdersUiState
    data class Success(val orders: List<ActiveOrder>) : ActiveOrdersUiState
}
