package com.coffeeshop.auth.internal.screen.register

import android.app.Activity
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.coffeeshop.auth.api.domain.usecase.RegisterByFirebaseIdTokenAndNameUseCase
import com.coffeeshop.auth.internal.data.firebase.FirebasePhoneAuthManager
import com.coffeeshop.auth.internal.screen.vmfactory.BaseAuthViewModel
import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.result.Result
import com.coffeeshop.utils.validateName
import retrofit2.HttpException
import com.coffeeshop.utils.validateRussianPhoneNumberBy_E_164
import dagger.assisted.AssistedFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal enum class RegisterError {
    SmsSendFailed, WrongCode, AlreadyRegistered, ServerError, NetworkError
}

@Stable
internal sealed interface RegisterUiState {

    val name: String
        get() = ""
    val phone: String
        get() = ""
    val isLoading: Boolean
        get() = false

    data class InputData(
        override val name: String = "",
        override val phone: String = "",
        val sendButtonEnabled: Boolean = false,
        override val isLoading: Boolean = false
    ) : RegisterUiState

    data class EnteringCode(
        override val name: String,
        override val phone: String,
        val smsCode: String = "",
        val timerSeconds: Int = 60,
        val isCodeError: Boolean = false,
        override val isLoading: Boolean = false,
        val resendEnabled: Boolean = false
    ) : RegisterUiState
}

internal sealed interface RegisterUiStateEvent {
    data class NameChanged(val name: String) : RegisterUiStateEvent
    data class PhoneChanged(val phone: String) : RegisterUiStateEvent
    data class SmsCodeChanged(val smsCode: String) : RegisterUiStateEvent
    data class SendSmsClicked(val activity: Activity) : RegisterUiStateEvent
    data class ResendSmsClicked(val activity: Activity) : RegisterUiStateEvent
}

internal class RegisterViewModel
@Inject constructor(
    private val firebasePhoneAuthManager: FirebasePhoneAuthManager,
    private val registerByFirebaseIdTokenAndName: RegisterByFirebaseIdTokenAndNameUseCase
) : BaseAuthViewModel() {

    private val scope = viewModelScope
    var currentPhone: String = ""
        private set
    var currentName: String = ""
        private set
    private var timerJob: Job? = null

    private val _uiState: MutableStateFlow<RegisterUiState> =
        MutableStateFlow(RegisterUiState.InputData())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _navigateToHome = Channel<Unit>(Channel.BUFFERED)
    val navigateToHome = _navigateToHome.receiveAsFlow()

    private val _error = Channel<RegisterError>(Channel.BUFFERED)
    val error = _error.receiveAsFlow()

    fun reduce(event: RegisterUiStateEvent) {
        when (event) {
            is RegisterUiStateEvent.NameChanged -> onNameChanged(event)
            is RegisterUiStateEvent.PhoneChanged -> onPhoneChanged(event)
            is RegisterUiStateEvent.SmsCodeChanged -> onSmsCodeChanged(event)
            is RegisterUiStateEvent.SendSmsClicked -> onSendSmsClicked(event)
            is RegisterUiStateEvent.ResendSmsClicked -> onSendSmsClicked(
                RegisterUiStateEvent.SendSmsClicked(
                    event.activity
                )
            )
        }
    }

    private fun onNameChanged(event: RegisterUiStateEvent.NameChanged) {
        currentName = event.name
        _uiState.update {
            RegisterUiState.InputData(
                name = currentName,
                phone = currentPhone,
                sendButtonEnabled = isSendEnabled()
            )
        }
    }

    private fun onPhoneChanged(event: RegisterUiStateEvent.PhoneChanged) {
        currentPhone = event.phone
        _uiState.update {
            RegisterUiState.InputData(
                name = currentName,
                phone = currentPhone,
                sendButtonEnabled = isSendEnabled()
            )
        }
    }

    private fun onSmsCodeChanged(event: RegisterUiStateEvent.SmsCodeChanged) {
        val state = _uiState.value as? RegisterUiState.EnteringCode ?: return
        val newCode = event.smsCode
        _uiState.update { state.copy(smsCode = newCode, isCodeError = false) }
        if (newCode.length == 6) registerWithCode(newCode)
    }

    private fun onSendSmsClicked(event: RegisterUiStateEvent.SendSmsClicked) {
        if (!isSendEnabled()) return
        _uiState.update { state ->
            when (state) {
                is RegisterUiState.InputData -> state.copy(isLoading = true)
                is RegisterUiState.EnteringCode -> state.copy(isLoading = true, resendEnabled = false)
            }
        }
        firebasePhoneAuthManager.sendVerificationCode(
            phoneNumber = "$RUSSIAN_PHONE_NUMBER_PREFIX$currentPhone",
            activity = event.activity,
            onCodeSent = {
                _uiState.update {
                    RegisterUiState.EnteringCode(name = currentName, phone = currentPhone)
                }
                startTimer()
            },
            onAutoVerified = { idToken ->
                scope.launch { registerWithIdToken(idToken) }
            },
            onError = {
                _uiState.update {
                    RegisterUiState.InputData(
                        name = currentName,
                        phone = currentPhone,
                        sendButtonEnabled = isSendEnabled()
                    )
                }
                scope.launch { _error.send(RegisterError.SmsSendFailed) }
            }
        )
    }

    private fun registerWithCode(rawCode: String) {
        val state = _uiState.value as? RegisterUiState.EnteringCode ?: return
        _uiState.update { state.copy(isLoading = true) }
        scope.launch {
            when (val result = firebasePhoneAuthManager.signInWithCode(rawCode)) {
                is Result.Success -> registerWithIdToken(result.data)
                is Result.Error -> {
                    _error.send(RegisterError.WrongCode)
                    _uiState.update { s ->
                        (s as? RegisterUiState.EnteringCode)?.copy(
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

    private suspend fun registerWithIdToken(idToken: String) {
        when (val result = registerByFirebaseIdTokenAndName(idToken = idToken, name = NameModel(currentName))) {
            is Result.Success -> {
                _navigateToHome.send(Unit)
                _uiState.update { RegisterUiState.InputData() }
            }
            is Result.Error -> {
                val httpCode = (result.exception as? HttpException)?.code()
                val registerError = when (httpCode) {
                    409 -> RegisterError.AlreadyRegistered
                    in 500..599 -> RegisterError.ServerError
                    else -> RegisterError.NetworkError
                }
                _error.send(registerError)
                _uiState.update { s ->
                    (s as? RegisterUiState.EnteringCode)?.copy(
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
                    (state as? RegisterUiState.EnteringCode)?.copy(timerSeconds = remaining)
                        ?: state
                }
            }
            _uiState.update { state ->
                (state as? RegisterUiState.EnteringCode)?.copy(resendEnabled = true) ?: state
            }
        }
    }

    private fun isSendEnabled(): Boolean =
        validateRussianPhoneNumberBy_E_164("$RUSSIAN_PHONE_NUMBER_PREFIX$currentPhone") && validateName(currentName)

    override fun onCleared() {
        timerJob?.cancel()
        scope.cancel()
        super.onCleared()
    }

    private companion object {
        const val TAG = "RegisterViewModel"
        const val RUSSIAN_PHONE_NUMBER_PREFIX = "+7"
    }
}