package com.coffeeshop.orderhistory.internal.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coffeeshop.common.result.Result
import com.coffeeshop.di.qualifiers.DispatcherMain
import com.coffeeshop.orderhistory.api.domain.model.OrderSummary
import com.coffeeshop.orderhistory.api.domain.usecase.GetOrderHistoryUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal sealed interface OrderHistoryUiState {
    data object Loading : OrderHistoryUiState
    data class Error(val message: String) : OrderHistoryUiState
    data class Success(val orders: List<OrderSummary> = emptyList()) : OrderHistoryUiState
}

internal sealed interface OrderHistoryUiStateEvent {
    data object Retry : OrderHistoryUiStateEvent
}

internal class OrderHistoryViewModel
@Inject constructor(
    private val getOrderHistory: GetOrderHistoryUseCase,
    @param:DispatcherMain private val mainDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderHistoryUiState>(OrderHistoryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun reduce(event: OrderHistoryUiStateEvent) {
        when (event) {
            OrderHistoryUiStateEvent.Retry -> loadHistory()
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            withContext(mainDispatcher) {
                _uiState.update { OrderHistoryUiState.Loading }
            }
            when (val result = getOrderHistory()) {
                is Result.Success -> withContext(mainDispatcher) {
                    _uiState.update { OrderHistoryUiState.Success(result.data) }
                }
                is Result.Error -> withContext(mainDispatcher) {
                    _uiState.update { OrderHistoryUiState.Error(result.exception.message ?: "Неизвестная ошибка") }
                }
                Result.Loading -> Unit
            }
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }
}
