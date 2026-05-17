package com.coffeeshop.cart.internal.screen.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arttttt.nav3router.Router
import com.coffeeshop.cart.api.domain.usecase.GetCartItemsUseCase
import com.coffeeshop.cart.api.domain.usecase.LoadCartDataUseCase
import com.coffeeshop.cart.api.domain.usecase.RemoveFromCartUseCase
import com.coffeeshop.cart.api.domain.usecase.SaveCartStateUseCase
import com.coffeeshop.common.model.products.ModifierCategory
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.model.support.display
import com.coffeeshop.di.qualifiers.DispatcherIO
import com.coffeeshop.logger.api.CoffeeshopLogger
import com.coffeeshop.logger.api.tagOf
import com.coffeshop.catalog.api.presentation.navigation.CatalogRoute
import com.coffeshop.navigation.Route
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal data class CartUiStateCartItemModifierBlock(
    val syrup: String = "НЕТ",
    val milk: String = "НЕТ",
    val vitamins: String = "НЕТ",
    val marshmallows: String = "НЕТ",
    val comment: String = "",
)

internal data class CartUiStateCartItem(
    val id: Long = 0L,
    val name: String = "",
    val photoUrl: String? = null,
    val volume: String = "",
    val price: String = "",
    val quantity: Int = 1,
    val modifiers: CartUiStateCartItemModifierBlock = CartUiStateCartItemModifierBlock(),
)

internal sealed interface CartUiState {
    data object Loading : CartUiState
    data class Error(val message: String) : CartUiState
    data class Success(
        val items: List<CartUiStateCartItem> = emptyList(),
        val totalPrice: String = ""
    ) : CartUiState
}

internal sealed interface CartUiStateEvent {
    data object LoadData : CartUiStateEvent
    data class RemoveFromCart(val uniqueCartItemID: ID) : CartUiStateEvent
    data object NavigateBack : CartUiStateEvent
    data object GoToPayment : CartUiStateEvent
}

internal class CartViewModel
@Inject constructor(
    private val loadCartData: LoadCartDataUseCase,
    private val getCartItems: GetCartItemsUseCase,
    private val saveCartState: SaveCartStateUseCase,
    private val removeFromCart: RemoveFromCartUseCase,
    @param:DispatcherIO private val dispatcher: CoroutineDispatcher,
    private val router: Router<Route>,
    private val logger: CoffeeshopLogger,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun reduce(event: CartUiStateEvent) {
        when (event) {
            CartUiStateEvent.LoadData -> onLoadData()
            is CartUiStateEvent.RemoveFromCart -> onRemoveFromCart(event.uniqueCartItemID)
            CartUiStateEvent.NavigateBack -> onNavigateBack()
            CartUiStateEvent.GoToPayment -> onGoToPayment()
        }
    }

    private fun onGoToPayment() {}

    private fun onNavigateBack() {
        logger.info(TAG.tagOf(), "onNavigateBack invoked")
        onCleared()

        router.popTo(CatalogRoute())
    }

    private fun onLoadData() {
        viewModelScope.launch(dispatcher) {
            try {
                loadCartData()
                getCartItems().collect { items ->
                    _uiState.update {
                        CartUiState.Success(
                            items = items.map { item ->
                                CartUiStateCartItem(
                                    id = item.uniqueCartItemID.value,
                                    name = item.productName.value,
                                    photoUrl = item.imageUrl,
                                    volume = item.size.display(),
                                    price = item.price.display(),
                                    quantity = item.quantity,
                                    modifiers = CartUiStateCartItemModifierBlock(
                                        syrup = item.selectedModifiers
                                            .find { it.category == ModifierCategory.SYRUP }?.name ?: "НЕТ",
                                        milk = item.selectedModifiers
                                            .find { it.category == ModifierCategory.ALT_MILK }?.name ?: "НЕТ",
                                        vitamins = item.selectedModifiers
                                            .find { it.category == ModifierCategory.VITAMIN_SHOT }?.name ?: "НЕТ",
                                        marshmallows = item.selectedModifiers
                                            .find { it.category == ModifierCategory.MARSHMALLOW }?.name ?: "НЕТ",
                                        comment = item.comment,
                                    ),
                                )
                            },
                            totalPrice = items
                                .fold(Price.emptyRublesPrice()) { acc, item -> acc + item.price }
                                .display(),
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { CartUiState.Error(e.message ?: "Ошибка загрузки корзины") }
            }
        }
    }

    private fun onRemoveFromCart(uniqueCartItemID: ID) {
        viewModelScope.launch(dispatcher) {
            removeFromCart(uniqueCartItemID)
        }
    }

    override fun onCleared() {
        CoroutineScope(SupervisorJob() + dispatcher).launch {
            saveCartState()
        }
        super.onCleared()
    }

    private companion object {
        const val TAG = "CartViewModel"
    }
}