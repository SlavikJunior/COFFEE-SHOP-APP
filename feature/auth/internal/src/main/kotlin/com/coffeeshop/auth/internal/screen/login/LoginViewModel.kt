package com.coffeeshop.auth.internal.screen.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.coffeeshop.auth.api.domain.usecase.SendSmsCodeByPhoneNumberUseCase
import com.coffeeshop.auth.api.domain.usecase.VerifySmsCodeByPhoneNumberUseCase
import com.coffeeshop.auth.internal.screen.vmfactory.BaseAuthViewModel
import com.coffeeshop.auth.internal.screen.vmfactory.SavedStateHandleFactory
import com.coffeeshop.common.model.auth.AuthStatus
import com.coffeeshop.common.model.auth.PhoneNumberModel
import com.coffeeshop.common.model.auth.SmsCodeModel
import com.coffeeshop.common.result.Result
import com.coffeeshop.utils.validateRussianPhoneNumberBy_E_164
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

internal sealed interface LoginUiState {

    data class InputPhone(
        val phone: String = "",
        val sendButtonEnabled: Boolean = false,
        val isLoading: Boolean = false
    ) : LoginUiState

    data class EnteringCode(
        val phone: String,
        val smsCode: String = "",
        val timerSeconds: Int = 60,
        val isCodeError: Boolean = false,
        val isLoading: Boolean = false,
        val resendEnabled: Boolean = false
    ) : LoginUiState
}

internal sealed interface LoginUiStateEvent {
    data class PhoneChanged(val phone: String) : LoginUiStateEvent
    data class SmsCodeChanged(val smsCode: String) : LoginUiStateEvent
    data object SendSmsClicked : LoginUiStateEvent
    data object ResendSmsClicked : LoginUiStateEvent
}

internal class LoginViewModel
@AssistedInject constructor(
    private val sendSmsCodeByPhoneNumber: SendSmsCodeByPhoneNumberUseCase,
    private val verifySmsCodeByPhoneNumber: VerifySmsCodeByPhoneNumberUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseAuthViewModel() {

    private val scope = viewModelScope
    var currentPhone: String = ""
        private set
    private var timerJob: Job? = null

    private val _uiState: MutableStateFlow<LoginUiState> =
        MutableStateFlow(LoginUiState.InputPhone())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigateToHome = Channel<Unit>(Channel.BUFFERED)
    val navigateToHome: Flow<Unit> = _navigateToHome.receiveAsFlow()

    fun reduce(event: LoginUiStateEvent) {
        when (event) {
            is LoginUiStateEvent.PhoneChanged -> onPhoneChanged(event)
            is LoginUiStateEvent.SmsCodeChanged -> onSmsCodeChanged(event)
            LoginUiStateEvent.SendSmsClicked -> onSendSmsClicked()
            LoginUiStateEvent.ResendSmsClicked -> onSendSmsClicked()
        }
    }

    private fun onPhoneChanged(event: LoginUiStateEvent.PhoneChanged) {
        currentPhone = event.phone
        _uiState.update {
            LoginUiState.InputPhone(
                phone = currentPhone,
                sendButtonEnabled = validateRussianPhoneNumberBy_E_164("+7$currentPhone")
            )
        }
    }

    private fun onSmsCodeChanged(event: LoginUiStateEvent.SmsCodeChanged) {
        val state = _uiState.value as? LoginUiState.EnteringCode ?: return
        val newCode = event.smsCode
        _uiState.update { state.copy(smsCode = newCode, isCodeError = false) }
        if (newCode.length == 6) verifyCode(newCode)
    }

    private fun onSendSmsClicked() {
        if (!validateRussianPhoneNumberBy_E_164("+7$currentPhone")) return
        _uiState.update { state ->
            when (state) {
                is LoginUiState.InputPhone -> state.copy(isLoading = true)
                is LoginUiState.EnteringCode -> state.copy(isLoading = true, resendEnabled = false)
            }
        }
        scope.launch {
            when (sendSmsCodeByPhoneNumber(PhoneNumberModel("+7$currentPhone"))) {
                is Result.Success -> {
                    _uiState.update { LoginUiState.EnteringCode(phone = currentPhone) }
                    startTimer()
                }
                is Result.Error -> _uiState.update {
                    LoginUiState.InputPhone(
                        phone = currentPhone,
                        sendButtonEnabled = true
                    )
                }
                Result.Loading -> Unit
            }
        }
    }

    private fun verifyCode(code: String) {
        val state = _uiState.value as? LoginUiState.EnteringCode ?: return
        _uiState.update { state.copy(isLoading = true) }
        scope.launch {
            when (verifySmsCodeByPhoneNumber(
                phoneNumber = PhoneNumberModel("+7$currentPhone"),
                smsCode = SmsCodeModel(code)
            )) {
                is Result.Success -> _navigateToHome.send(Unit)
                is Result.Error -> _uiState.update { s ->
                    (s as? LoginUiState.EnteringCode)?.copy(
                        isLoading = false,
                        isCodeError = true,
                        smsCode = ""
                    ) ?: s
                }
                Result.Loading -> Unit
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            var remaining = 60
            while (remaining > 0) {
                delay(1000)
                remaining--
                _uiState.update { state ->
                    (state as? LoginUiState.EnteringCode)?.copy(timerSeconds = remaining) ?: state
                }
            }
            _uiState.update { state ->
                (state as? LoginUiState.EnteringCode)?.copy(resendEnabled = true) ?: state
            }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        scope.cancel()
        super.onCleared()
    }

    @AssistedFactory
    interface Factory : SavedStateHandleFactory<LoginViewModel>

    companion object {
        val previewFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(
                    sendSmsCodeByPhoneNumber = object : SendSmsCodeByPhoneNumberUseCase {
                        override suspend fun invoke(phoneNumber: PhoneNumberModel) =
                            Result.Success(AuthStatus.WaitSms)
                    },
                    verifySmsCodeByPhoneNumber = object : VerifySmsCodeByPhoneNumberUseCase {
                        override suspend fun invoke(
                            phoneNumber: PhoneNumberModel,
                            smsCode: SmsCodeModel
                        ) = Result.Success(false)
                    },
                    savedStateHandle = SavedStateHandle()
                ) as T
            }
        }
    }
}