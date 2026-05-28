package com.coffeeshop.activeorders.internal.screen.activeorders

import androidx.compose.runtime.Stable
import com.coffeeshop.activeorders.api.domain.model.ActiveOrder

@Stable
internal sealed interface ActiveOrdersUiState {
    data object Loading : ActiveOrdersUiState
    data class Error(val message: String) : ActiveOrdersUiState
    data class Success(val orders: List<ActiveOrder>) : ActiveOrdersUiState
}
