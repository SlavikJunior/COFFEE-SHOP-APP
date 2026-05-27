package com.coffeeshop.profile.internal.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arttttt.nav3router.Router
import com.coffeeshop.common.model.user.User
import com.coffeeshop.common.result.Result
import com.coffeeshop.di.qualifiers.DispatcherMain
import com.coffeeshop.orderhistory.api.presentation.navigation.OrderHistoryRoute
import com.coffeeshop.profile.api.domain.usecase.GetProfileUseCase
import com.coffeeshop.profile.api.domain.usecase.LogoutUseCase
import com.coffeeshop.utils.orDefault
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

internal sealed interface ProfileUiStateStatus {
    data object Loading : ProfileUiStateStatus
    data object Success : ProfileUiStateStatus
    data class Error(val cause: Throwable? = null) : ProfileUiStateStatus
}

internal data class ProfileUiState(
    val status: ProfileUiStateStatus = ProfileUiStateStatus.Loading,
    val name: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val snackbarMessage: String? = null,
)

internal sealed interface ProfileUiEvent {
    data object OpenOrderHistory : ProfileUiEvent
    data object Logout : ProfileUiEvent
    data object NotificationsStub : ProfileUiEvent
    data object FeedbackStub : ProfileUiEvent
    data object DismissSnackbar : ProfileUiEvent
    data object NavigateBack : ProfileUiEvent
}

internal class ProfileViewModel
@Inject constructor(
    private val getProfile: GetProfileUseCase,
    private val logout: LogoutUseCase,
    private val router: Router<Route>,
    @param:DispatcherMain private val mainDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun reduce(event: ProfileUiEvent) {
        when (event) {
            ProfileUiEvent.OpenOrderHistory -> router.push(OrderHistoryRoute)
            ProfileUiEvent.Logout -> onLogout()
            ProfileUiEvent.NotificationsStub -> showStubMessage()
            ProfileUiEvent.FeedbackStub -> showStubMessage()
            ProfileUiEvent.DismissSnackbar -> _uiState.update { it.copy(snackbarMessage = null) }
            ProfileUiEvent.NavigateBack -> onCloseClick()
        }
    }

    private fun onCloseClick() = router.pop()

    private fun loadProfile() {
        viewModelScope.launch {
            withContext(mainDispatcher) {
                _uiState.update { it.copy(status = ProfileUiStateStatus.Loading) }
            }
            when (val result = getProfile()) {
                is Result.Success<User> -> withContext(mainDispatcher) {
                    val user = result.data
                    _uiState.update { state ->
                        state.copy(
                            status = ProfileUiStateStatus.Success,
                            name = user.userName.value,
                            email = user.userEmail?.value.orDefault(default = DEFAULT_EMAIL),
                            phoneNumber = user.userPhone.value,
                        )
                    }
                }
                is Result.Error -> withContext(mainDispatcher) {
                    _uiState.update { it.copy(status = ProfileUiStateStatus.Error(result.exception)) }
                }
                Result.Loading -> Unit
            }
        }
    }

    private fun onLogout() {
        viewModelScope.launch {
            val result = logout()
            if (result is Result.Success) {
                withContext(mainDispatcher) {
                    router.replaceCurrent(CatalogRoute(isLoggedIn = false))
                }
            }
        }
    }


    private fun showStubMessage() {
        _uiState.update { it.copy(snackbarMessage = STUB_MESSAGE) }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    private companion object {
        const val DEFAULT_EMAIL = "ПОЧТА НЕ ЗАДАНА"
        const val STUB_MESSAGE = "Будет добавлено позже"
    }
}
