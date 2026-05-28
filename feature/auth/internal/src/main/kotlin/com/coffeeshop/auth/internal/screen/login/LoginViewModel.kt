package com.coffeeshop.auth.internal.screen.login

import android.app.Activity
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.coffeeshop.auth.api.domain.usecase.VerifyFirebaseTokenUseCase
import com.coffeeshop.auth.internal.data.firebase.FirebasePhoneAuthManager
import com.coffeeshop.auth.internal.screen.vmfactory.BaseAuthViewModel
import com.coffeeshop.common.result.Result
import com.coffeeshop.utils.validateRussianPhoneNumberBy_E_164
import kotlinx.coroutines.Job
import retrofit2.HttpException
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
import javax.inject.Inject

internal enum class LoginError {
    SmsSendFailed, WrongCode, AccountNotFound, ServerError, NetworkError
}

@Stable
internal sealed interface LoginUiState {

    val isLoading: Boolean

    data class InputPhone(
        val phone: String = "",
        val sendButtonEnabled: Boolean = false,
        override val isLoading: Boolean = false
    ) : LoginUiState

    data class EnteringCode(
        val phone: String,
        val smsCode: String = "",
        val timerSeconds: Int = 60,
        val isCodeError: Boolean = false,
        override val isLoading: Boolean = false,
        val resendEnabled: Boolean = false
    ) : LoginUiState
}

internal sealed interface LoginUiStateEvent {
    data class PhoneChanged(val phone: String) : LoginUiStateEvent
    data class SmsCodeChanged(val smsCode: String) : LoginUiStateEvent
    data class SendSmsClicked(val activity: Activity) : LoginUiStateEvent
    data class ResendSmsClicked(val activity: Activity) : LoginUiStateEvent
}

internal class LoginViewModel
@Inject constructor(
    private val firebasePhoneAuthManager: FirebasePhoneAuthManager,
    private val verifySmsCodeByPhoneNumber: VerifyFirebaseTokenUseCase
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

    private val _error = Channel<LoginError>(Channel.BUFFERED)
    val error = _error.receiveAsFlow()

    fun reduce(event: LoginUiStateEvent) {
        when (event) {
            is LoginUiStateEvent.PhoneChanged -> onPhoneChanged(event)
            is LoginUiStateEvent.SmsCodeChanged -> onSmsCodeChanged(event)
            is LoginUiStateEvent.SendSmsClicked -> onSendSmsClicked(event)
            is LoginUiStateEvent.ResendSmsClicked -> onSendSmsClicked(
                LoginUiStateEvent.SendSmsClicked(
                    event.activity
                )
            )
        }
    }

    private fun onPhoneChanged(event: LoginUiStateEvent.PhoneChanged) {
        currentPhone = event.phone
        _uiState.update {
            LoginUiState.InputPhone(
                phone = currentPhone,
                sendButtonEnabled = validateRussianPhoneNumberBy_E_164("$RUSSIAN_PHONE_NUMBER_PREFIX$currentPhone")
            )
        }
    }

    private fun onSmsCodeChanged(event: LoginUiStateEvent.SmsCodeChanged) {
        val state = _uiState.value as? LoginUiState.EnteringCode ?: return
        val newCode = event.smsCode
        _uiState.update { state.copy(smsCode = newCode, isCodeError = false) }
        if (newCode.length == 6) verifyCode(newCode)
    }

    private fun onSendSmsClicked(event: LoginUiStateEvent.SendSmsClicked) {
        if (!validateRussianPhoneNumberBy_E_164("$RUSSIAN_PHONE_NUMBER_PREFIX$currentPhone")) return
        _uiState.update { state ->
            when (state) {
                is LoginUiState.InputPhone -> state.copy(isLoading = true)
                is LoginUiState.EnteringCode -> state.copy(isLoading = true, resendEnabled = false)
            }
        }
        firebasePhoneAuthManager.sendVerificationCode(
            phoneNumber = "$RUSSIAN_PHONE_NUMBER_PREFIX$currentPhone",
            activity = event.activity,
            onCodeSent = {
                _uiState.update { LoginUiState.EnteringCode(phone = currentPhone) }
                startTimer()
            },
            onAutoVerified = { idToken ->
                scope.launch { verifyWithIdToken(idToken) }
            },
            onError = {
                _uiState.update {
                    LoginUiState.InputPhone(phone = currentPhone, sendButtonEnabled = true)
                }
                scope.launch { _error.send(LoginError.SmsSendFailed) }
            }
        )
    }

    private fun verifyCode(rawCode: String) {
        val state = _uiState.value as? LoginUiState.EnteringCode ?: return
        _uiState.update { state.copy(isLoading = true) }
        scope.launch {
            when (val result = firebasePhoneAuthManager.signInWithCode(rawCode)) {
                is Result.Success -> verifyWithIdToken(result.data)
                is Result.Error -> {
                    _error.send(LoginError.WrongCode)
                    _uiState.update { s ->
                        (s as? LoginUiState.EnteringCode)?.copy(
                            isLoading = false,
                            isCodeError = true,
                            smsCode = ""
                        ) ?: s
                    }
                }
                Result.Loading -> Unit
            }
        }
    }

    private suspend fun verifyWithIdToken(idToken: String) {
        when (val result = verifySmsCodeByPhoneNumber(idToken)) {
            is Result.Success -> {
                if (result.data) {
                    _navigateToHome.send(Unit)
                    _uiState.update { LoginUiState.InputPhone() }
                } else {
                    _error.send(LoginError.AccountNotFound)
                    _uiState.update { s ->
                        (s as? LoginUiState.EnteringCode)?.copy(
                            isLoading = false,
                            isCodeError = false,
                            smsCode = ""
                        ) ?: LoginUiState.InputPhone(phone = currentPhone, sendButtonEnabled = true)
                    }
                }
            }
            is Result.Error -> {
                val loginError = if ((result.exception as? HttpException)?.code() in 500..599)
                    LoginError.ServerError else LoginError.NetworkError
                _error.send(loginError)
                _uiState.update { s ->
                    (s as? LoginUiState.EnteringCode)?.copy(
                        isLoading = false,
                        isCodeError = false,
                        smsCode = ""
                    ) ?: s
                }
            }
            Result.Loading -> Unit
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

    private companion object {
        const val TAG = "LoginViewModel"
        const val RUSSIAN_PHONE_NUMBER_PREFIX = "+7"
    }
}