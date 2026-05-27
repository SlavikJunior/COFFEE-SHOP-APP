package com.coffeeshop.activeorders.internal.screen.activeorders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arttttt.nav3router.Router
import com.coffeeshop.activeorders.api.domain.usecase.GetActiveOrdersUseCase
import com.coffeeshop.common.events.OrderEventBus
import com.coffeeshop.common.result.Result
import com.coffeeshop.di.qualifiers.DispatcherMain
import com.coffeshop.navigation.Route
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class ActiveOrdersViewModel
@Inject constructor(
    private val getActiveOrders: GetActiveOrdersUseCase,
    private val router: Router<Route>,
    @param:DispatcherMain private val mainDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ActiveOrdersUiState>(ActiveOrdersUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadOrders()
        viewModelScope.launch {
            OrderEventBus.orderStatusChanged.collect { loadOrders() }
        }
    }

    fun reduce(event: ActiveOrdersUiStateEvent) {
        when (event) {
            ActiveOrdersUiStateEvent.Retry -> loadOrders()
            ActiveOrdersUiStateEvent.NavigateBack -> router.pop()
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            withContext(mainDispatcher) {
                _uiState.update { ActiveOrdersUiState.Loading }
            }
            when (val result = getActiveOrders()) {
                is Result.Success -> withContext(mainDispatcher) {
                    _uiState.update { ActiveOrdersUiState.Success(result.data) }
                }
                is Result.Error -> withContext(mainDispatcher) {
                    _uiState.update { ActiveOrdersUiState.Error(result.exception.message ?: "Ошибка загрузки") }
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
