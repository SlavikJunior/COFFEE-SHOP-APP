package com.coffeeshop.profile.internal.screen

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arttttt.nav3router.Router
import com.coffeeshop.activeorders.api.presentation.navigation.ActiveOrdersRoute
import com.coffeeshop.auth.api.domain.usecase.IsUserLoggedInUseCase
import com.coffeeshop.auth.api.presentation.navigation.LoginRoute
import com.coffeeshop.common.model.user.User
import com.coffeeshop.common.result.Result
import com.coffeeshop.di.qualifiers.DispatcherMain
import com.coffeeshop.orderhistory.api.presentation.navigation.OrderHistoryRoute
import com.coffeeshop.profile.api.domain.usecase.GetProfileUseCase
import com.coffeeshop.profile.api.domain.usecase.LogoutUseCase
import com.coffeeshop.profile.api.presentation.navigation.ProfileRoute
import com.coffeeshop.utils.orDefault
import com.coffeshop.catalog.api.presentation.navigation.CatalogRoute
import com.coffeshop.navigation.Route
import com.github.slavikjunior.favorites.api.navigation.FavoritesRoute
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

@Stable
internal data class ProfileUiState(
    val status: ProfileUiStateStatus = ProfileUiStateStatus.Loading,
    val name: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val snackbarMessage: String? = null,
)

internal sealed interface ProfileUiStateEvent {
    data object OpenOrderHistory : ProfileUiStateEvent
    data object Logout : ProfileUiStateEvent
    data object NotificationsStub : ProfileUiStateEvent
    data object FeedbackStub : ProfileUiStateEvent
    data object DismissSnackbar : ProfileUiStateEvent
    data object CloseClick : ProfileUiStateEvent
    data object BottomNavigateToFavorites : ProfileUiStateEvent
    data object BottomNavigateToCatalog : ProfileUiStateEvent
    data object BottomNavigateToActiveOrders : ProfileUiStateEvent
}

internal class ProfileViewModel
@Inject constructor(
    private val getProfile: GetProfileUseCase,
    private val logout: LogoutUseCase,
    private val router: Router<Route>,
    private val isUserLoggedIn: IsUserLoggedInUseCase,
    @param:DispatcherMain private val mainDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun reduce(event: ProfileUiStateEvent) {
        when (event) {
            ProfileUiStateEvent.OpenOrderHistory -> router.push(OrderHistoryRoute)
            ProfileUiStateEvent.Logout -> onLogout()
            ProfileUiStateEvent.NotificationsStub -> showStubMessage()
            ProfileUiStateEvent.FeedbackStub -> showStubMessage()
            ProfileUiStateEvent.DismissSnackbar -> _uiState.update { it.copy(snackbarMessage = null) }
            ProfileUiStateEvent.CloseClick -> onCloseClick()
            ProfileUiStateEvent.BottomNavigateToFavorites -> onBottomNavigateToFavorites()
            ProfileUiStateEvent.BottomNavigateToCatalog -> onBottomNavigateToCatalog()
            ProfileUiStateEvent.BottomNavigateToActiveOrders -> onBottomNavigateToActiveOrders()
        }
    }

    private fun onBottomNavigateToActiveOrders() =
        if (isUserLoggedIn()) router.replaceStack(ActiveOrdersRoute)
        else router.push(LoginRoute(message = LOGIN_NAVIGATE_MESSAGE))

    private fun onBottomNavigateToCatalog() = router.replaceStack(CatalogRoute())

    private fun onBottomNavigateToFavorites() {
        // todo()
        //router.replaceStack(FavoritesRoute)
    }

    private fun onCloseClick() = router.replaceStack(CatalogRoute())

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
                is Result.Error -> onAutoLogout(PROFILE_LOAD_ERROR_MESSAGE)
                Result.Loading -> Unit
            }
        }
    }

    private suspend fun onAutoLogout(message: String) {
        logout()
        withContext(mainDispatcher) {
            router.push(LoginRoute(message = message))
        }
    }

    private fun onLogout() {
        viewModelScope.launch {
            logout()
            withContext(mainDispatcher) {
                router.replaceCurrent(CatalogRoute(isLoggedIn = false))
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
        const val LOGIN_NAVIGATE_MESSAGE = "Для начала войдите в систему."
        const val PROFILE_LOAD_ERROR_MESSAGE = "Не удалось загрузить профиль. Войдите снова."
        const val DEFAULT_EMAIL = "ПОЧТА НЕ ЗАДАНА"
        const val STUB_MESSAGE = "Будет добавлено позже."
    }
}
