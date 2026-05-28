package com.coffeeshop.activeorders.internal.screen.activeorders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arttttt.nav3router.Router
import com.coffeeshop.activeorders.api.domain.model.ActiveOrder
import com.coffeeshop.activeorders.api.domain.repository.ActiveOrdersRepository
import com.coffeeshop.activeorders.api.domain.usecase.GetActiveOrdersUseCase
import com.coffeeshop.activeorders.api.presentation.navigation.ActiveOrdersRoute
import com.coffeeshop.common.events.OrderEventBus
import com.coffeeshop.common.events.OrderStatusUpdate
import com.coffeeshop.common.result.Result
import com.coffeeshop.di.qualifiers.DispatcherMain
import com.coffeeshop.profile.api.presentation.navigation.ProfileRoute
import com.coffeshop.catalog.api.presentation.navigation.CatalogRoute
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
    private val repository: ActiveOrdersRepository,
    private val router: Router<Route>,
    @param:DispatcherMain private val mainDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ActiveOrdersUiState>(ActiveOrdersUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadOrders()
        viewModelScope.launch {
            OrderEventBus.orderStatusChanged.collect { update ->
                applyStatusUpdate(update)
            }
        }
    }

    fun reduce(event: ActiveOrdersUiStateEvent) {
        when (event) {
            ActiveOrdersUiStateEvent.Retry -> loadOrders()
            ActiveOrdersUiStateEvent.NavigateBack -> router.replaceStack(CatalogRoute())
            ActiveOrdersUiStateEvent.BottomNavigateToCatalog -> onBottomNavigateToCatalog()
            ActiveOrdersUiStateEvent.BottomNavigateToFavorites -> onBottomNavigateToFavorites()
            ActiveOrdersUiStateEvent.BottomNavigateToProfile -> onBottomNavigateToProfile()
        }
    }

    private fun onBottomNavigateToCatalog() = router.replaceStack(CatalogRoute())

    private fun onBottomNavigateToProfile() = router.replaceStack(ProfileRoute())

    private fun onBottomNavigateToFavorites() {
        // todo()
        //router.replaceStack(FavoritesRoute)
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

    private suspend fun applyStatusUpdate(update: OrderStatusUpdate) {
        val current = _uiState.value
        if (current !is ActiveOrdersUiState.Success) return

        val existing = current.orders.firstOrNull { it.id == update.orderId }
        if (existing != null) {
            val newList = current.orders.map { order ->
                if (order.id == update.orderId) order.copy(status = update.status) else order
            }
            withContext(mainDispatcher) {
                _uiState.update { ActiveOrdersUiState.Success(newList) }
            }
        } else {
            when (val result = repository.fetchOrder(update.orderId)) {
                is Result.Success -> {
                    val newList = current.orders + result.data
                    withContext(mainDispatcher) {
                        _uiState.update { ActiveOrdersUiState.Success(newList) }
                    }
                }
                is Result.Error -> Log.w(TAG, "fetchOrder(${update.orderId}) failed", result.exception)
                Result.Loading -> Unit
            }
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    private companion object {
        const val TAG = "ActiveOrdersViewModel"
    }
}
