package com.coffeeshop.profile.internal.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arttttt.nav3router.Router
import com.coffeeshop.common.model.order.Order
import com.coffeeshop.common.model.order.OrderItem
import com.coffeeshop.common.model.order.OrderStatus
import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.model.support.MessageFromUser
import com.coffeeshop.common.model.support.Size
import com.coffeeshop.common.model.user.User
import com.coffeeshop.common.model.user.orEmpty
import com.coffeeshop.common.result.Result
import com.coffeeshop.profile.api.domain.usecase.ChangeEmailUseCase
import com.coffeeshop.profile.api.domain.usecase.ChangeNameUseCase
import com.coffeeshop.profile.api.domain.usecase.ChangePhoneNumberUseCase
import com.coffeeshop.profile.api.domain.usecase.GetOrderHistoryUseCase
import com.coffeeshop.profile.api.domain.usecase.GetProfileUseCase
import com.coffeeshop.profile.api.domain.usecase.LogoutUseCase
import com.coffeeshop.profile.api.domain.usecase.SendFeedBackUseCase
import com.coffeeshop.profile.api.domain.usecase.ToggleGetNotificationsUseCase
import com.coffeshop.navigation.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal data class ProfileOrderItemView(
    val product: Product,
    val size: Size,
    val quantity: Int,
    val modifiersNames: List<String>
)

internal fun OrderItem.toProfileOrderItem(): ProfileOrderItemView =
    ProfileOrderItemView(
        product = this.product,
        size = this.size,
        quantity = this.quantity,
        modifiersNames = this.modifiers.map { it.additiveName.value }
    )

internal data class ProfileOrderView(
    val status: OrderStatus,
    val comment: String,
    val items: List<ProfileOrderItemView>,
    val createdAt: String
)

internal fun Order.toProfileOrderView(): ProfileOrderView =
    ProfileOrderView(
        status = this.orderStatus,
        comment = this.comment.orEmpty(),
        items = this.items.map { it.toProfileOrderItem() },
        createdAt = TODO()
    )

// TODO("Придумать более корректное название для состояния состояния")
internal sealed interface ProfileUiStateState {
    data object Loading : ProfileUiStateState
    data object Success : ProfileUiStateState
    data class Error(val cause: Throwable? = null) : ProfileUiStateState
}

internal data class ProfileUiState(
    val profileUiStateState: ProfileUiStateState = ProfileUiStateState.Loading,
    val isLoggedIn: Boolean = true,
    val name: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val isNotificationsEnabled: Boolean = true,
    val orderHistory: List<ProfileOrderView> = listOf()
)

internal fun ProfileUiState.asLoadingState(): ProfileUiState =
    this.copy(profileUiStateState = ProfileUiStateState.Loading)

internal fun ProfileUiState.asErrorState(cause: Throwable?): ProfileUiState =
    this.copy(profileUiStateState = ProfileUiStateState.Error(cause = cause))

sealed interface ProfileUiStateEvent {

    data class ChangeEmail(val newEmail: String) : ProfileUiStateEvent

    data class ChangeName(val newName: String) : ProfileUiStateEvent

    data class ChangePhoneNumber(val newPhoneNumber: String) : ProfileUiStateEvent

    data object GetOrderHistory : ProfileUiStateEvent

    data object GetProfile : ProfileUiStateEvent

    data object Logout : ProfileUiStateEvent

    data class SendFeedBack(val messageFromUser: MessageFromUser) : ProfileUiStateEvent

    data object ToggleGetNotifications : ProfileUiStateEvent
}

internal class ProfileViewModel
@Inject constructor(
    private val changeEmail: ChangeEmailUseCase,
    private val changeName: ChangeNameUseCase,
    private val changePhoneNumber: ChangePhoneNumberUseCase,
    private val getOrderHistory: GetOrderHistoryUseCase,
    private val getProfile: GetProfileUseCase,
    private val logout: LogoutUseCase,
    private val sendFeedBack: SendFeedBackUseCase,
    private val toggleGetNotifications: ToggleGetNotificationsUseCase,
//    private val router: Router<Route>
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private var user: User? = null

    init {
        loadData()
    }

    fun reduce(event: ProfileUiStateEvent) {
        when (event) {
            is ProfileUiStateEvent.ChangeEmail -> TODO()
            is ProfileUiStateEvent.ChangeName -> TODO()
            is ProfileUiStateEvent.ChangePhoneNumber -> TODO()
            ProfileUiStateEvent.GetOrderHistory -> TODO()
            ProfileUiStateEvent.GetProfile -> TODO()
            ProfileUiStateEvent.Logout -> TODO()
            is ProfileUiStateEvent.SendFeedBack -> TODO()
            ProfileUiStateEvent.ToggleGetNotifications -> TODO()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val result = getProfile()
            var isSuccess = false

            withContext(Dispatchers.Main) {
                isSuccess = when (result) {
                    is Result.Success<User> -> {
                        user = result.data

                        _uiState.update { currentState ->
                            currentState.copy(
                                profileUiStateState = ProfileUiStateState.Success,
                                name = user!!.userName.value,
                                email = user!!.userEmail.orEmpty(),
                                phoneNumber = user!!.userPhone.value,
                                isNotificationsEnabled = user!!.notificationsEnabled,
                                orderHistory = listOf()
                            )
                        }

                        true
                    }

                    is Result.Error -> {
                        _uiState.update { it.asErrorState(result.exception) }
                        false
                    }

                    Result.Loading -> {
                        _uiState.update { it.asLoadingState() }
                        false
                    }
                }
            }

            if (isSuccess) {
                val orders: Flow<Result<List<Order>>> = getOrderHistory()
                orders.collect { orders ->
                    when (orders) {
                        Result.Loading -> {
                            withContext(Dispatchers.Main) {
                                _uiState.update { it.asLoadingState() }
                            }
                        }

                        is Result.Error -> {
                            withContext(Dispatchers.Main) {
                                _uiState.update { it.asErrorState(orders.exception) }
                            }
                        }

                        is Result.Success<List<Order>> -> {
                            withContext(Dispatchers.Main) {
                                _uiState.update { currentState ->
                                    currentState.copy(orderHistory = orders.data.map { order -> order.toProfileOrderView() })
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        viewModelScope.cancel("$TAG onCleared")

        super.onCleared()
    }

    private companion object {
        const val TAG = "ProfileViewModel"
    }
}