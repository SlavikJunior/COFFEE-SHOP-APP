package com.coffeeshop.auth.internal.screen.register

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.coffeeshop.auth.api.domain.usecase.RegisterByPhoneNumberAndNameAndSmsCodeUseCase
import com.coffeeshop.auth.api.domain.usecase.SendSmsCodeByPhoneNumberUseCase
import com.coffeeshop.auth.internal.screen.vmfactory.BaseAuthViewModel
import com.coffeeshop.auth.internal.screen.vmfactory.SavedStateHandleFactory
import com.coffeeshop.common.model.auth.AuthStatus
import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.model.auth.PhoneNumberModel
import com.coffeeshop.common.model.auth.SmsCodeModel
import com.coffeeshop.common.result.Result
import com.coffeeshop.utils.validateName
import com.coffeeshop.utils.validateRussianPhoneNumberBy_E_164
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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
import kotlin.reflect.KClass

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
    data object SendSmsClicked : RegisterUiStateEvent
    data object ResendSmsClicked : RegisterUiStateEvent
}

internal class RegisterViewModel
@AssistedInject constructor(
    private val sendSmsCodeByPhoneNumber: SendSmsCodeByPhoneNumberUseCase,
    private val registerByPhoneNumberAndNameAndSmsCode: RegisterByPhoneNumberAndNameAndSmsCodeUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle
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

    fun reduce(event: RegisterUiStateEvent) {
        when (event) {
            is RegisterUiStateEvent.NameChanged -> onNameChanged(event)
            is RegisterUiStateEvent.PhoneChanged -> onPhoneChanged(event)
            is RegisterUiStateEvent.SmsCodeChanged -> onSmsCodeChanged(event)
            RegisterUiStateEvent.SendSmsClicked -> onSendSmsClicked()
            RegisterUiStateEvent.ResendSmsClicked -> onSendSmsClicked()
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

    private fun onSendSmsClicked() {
        if (!isSendEnabled()) return
        _uiState.update { state ->
            when (state) {
                is RegisterUiState.InputData -> state.copy(isLoading = true)
                is RegisterUiState.EnteringCode -> state.copy(isLoading = true, resendEnabled = false)
            }
        }
        scope.launch {
            when (sendSmsCodeByPhoneNumber(PhoneNumberModel("+7$currentPhone"))) {
                is Result.Success -> {
                    _uiState.update {
                        RegisterUiState.EnteringCode(name = currentName, phone = currentPhone)
                    }
                    startTimer()
                }
                is Result.Error -> _uiState.update {
                    RegisterUiState.InputData(
                        name = currentName,
                        phone = currentPhone,
                        sendButtonEnabled = isSendEnabled()
                    )
                }
                Result.Loading -> Unit
            }
        }
    }

    private fun registerWithCode(code: String) {
        val state = _uiState.value as? RegisterUiState.EnteringCode ?: return
        _uiState.update { state.copy(isLoading = true) }
        scope.launch {
            when (registerByPhoneNumberAndNameAndSmsCode(
                phoneNumber = PhoneNumberModel("+7$currentPhone"),
                name = NameModel(currentName),
                smsCode = SmsCodeModel(code)
            )) {
                is Result.Success -> _navigateToHome.send(Unit)
                is Result.Error -> _uiState.update { s ->
                    (s as? RegisterUiState.EnteringCode)?.copy(
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
                    (state as? RegisterUiState.EnteringCode)?.copy(timerSeconds = remaining) ?: state
                }
            }
            _uiState.update { state ->
                (state as? RegisterUiState.EnteringCode)?.copy(resendEnabled = true) ?: state
            }
        }
    }

    private fun isSendEnabled(): Boolean =
        validateRussianPhoneNumberBy_E_164("+7$currentPhone") && validateName(currentName)

    override fun onCleared() {
        timerJob?.cancel()
        scope.cancel()
        super.onCleared()
    }

    @AssistedFactory
    interface Factory : SavedStateHandleFactory<RegisterViewModel>

    companion object {
        val previewFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
                @Suppress("UNCHECKED_CAST")
                return RegisterViewModel(
                    sendSmsCodeByPhoneNumber = object : SendSmsCodeByPhoneNumberUseCase {
                        override suspend fun invoke(phoneNumber: PhoneNumberModel) =
                            Result.Success(AuthStatus.WaitSms)
                    },
                    registerByPhoneNumberAndNameAndSmsCode = object :
                        RegisterByPhoneNumberAndNameAndSmsCodeUseCase {
                        override suspend fun invoke(
                            phoneNumber: PhoneNumberModel,
                            name: NameModel,
                            smsCode: SmsCodeModel
                        ) = Result.Success(AuthStatus.User)
                    },
                    savedStateHandle = SavedStateHandle()
                ) as T
            }
        }
    }
}