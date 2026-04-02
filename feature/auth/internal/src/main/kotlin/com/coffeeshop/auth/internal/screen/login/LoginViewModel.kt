package com.coffeeshop.auth.internal.screen.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.coffeeshop.auth.api.domain.exception.AuthException
import com.coffeeshop.auth.api.domain.usecase.SendSmsCodeByPhoneNumberUseCase
import com.coffeeshop.auth.api.domain.usecase.VerifySmsCodeByPhoneNumberUseCase
import com.coffeeshop.common.model.AuthStatus
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.model.PhoneNumberModel
import com.coffeeshop.common.model.SmsCodeModel
import com.coffeshop.utils.validateRussianPhoneNumberBy_E_164
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KClass

internal sealed interface MyLoginUiState {

    data object WaitSmsCodeUiState : MyLoginUiState

    data class VerifySmsCodeUiState(
        val smsCode: String = "",
        val verifyButtonEnabled: Boolean = false
    ) : MyLoginUiState

    data class InputPhoneUiState(
        val phone: String = "+7",
        val sendButtonEnabled: Boolean = false
    ) : MyLoginUiState

    data class ErrorUiState(
        val errorMessage: String = "",
        val cause: AuthException? = null
    ) : MyLoginUiState
}

internal sealed interface LoginUiStateEvent {
    data class PhoneChangeEvent(val phone: String) : LoginUiStateEvent
    data class SmsCodeChangeEvent(val smsCode: String) : LoginUiStateEvent
    data object SendSmsButtonClicked : LoginUiStateEvent
    data object VerifyButtonClicked : LoginUiStateEvent
}

internal class MyLoginViewModel
@Inject constructor(
    private val sendSmsCodeByPhoneNumber: SendSmsCodeByPhoneNumberUseCase,
    private val verifySmsCodeByPhoneNumber: VerifySmsCodeByPhoneNumberUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val scope = viewModelScope
    private var currentPhone: String = "+7"

    private val _uiState: MutableStateFlow<MyLoginUiState> =
        MutableStateFlow(MyLoginUiState.InputPhoneUiState())
    val uiState: StateFlow<MyLoginUiState> = _uiState.asStateFlow()

    fun reduce(event: LoginUiStateEvent) {
        when (event) {
            is LoginUiStateEvent.PhoneChangeEvent -> onChangePhone(event)
            is LoginUiStateEvent.SmsCodeChangeEvent -> onSmsCodeChanged(event)
            is LoginUiStateEvent.SendSmsButtonClicked -> onSendSmsButtonClicked()
            is LoginUiStateEvent.VerifyButtonClicked -> onVerifyButtonClicked()
        }
    }

    private fun onChangePhone(event: LoginUiStateEvent.PhoneChangeEvent) {
        currentPhone = event.phone
        _uiState.update {
            MyLoginUiState.InputPhoneUiState(
                phone = currentPhone,
                sendButtonEnabled = validateRussianPhoneNumberBy_E_164(currentPhone)
            )
        }
    }

    private fun onSmsCodeChanged(event: LoginUiStateEvent.SmsCodeChangeEvent) {
        _uiState.update {
            MyLoginUiState.VerifySmsCodeUiState(
                smsCode = event.smsCode,
                verifyButtonEnabled = event.smsCode.length == 6
            )
        }
    }

    private fun onSendSmsButtonClicked() {
        if (!validateRussianPhoneNumberBy_E_164(currentPhone)) {
            _uiState.update { MyLoginUiState.ErrorUiState(cause = AuthException.InvalidInputDataFormat()) }
            return
        }
        _uiState.update { MyLoginUiState.WaitSmsCodeUiState }
        scope.launch {
            when (val result = sendSmsCodeByPhoneNumber(PhoneNumberModel(currentPhone))) {
                is Result.Success -> _uiState.update { MyLoginUiState.VerifySmsCodeUiState() }
                is Result.Error -> _uiState.update {
                    MyLoginUiState.ErrorUiState(
                        errorMessage = result.exception.message ?: "Ошибка отправки СМС",
                        cause = result.exception as? AuthException
                    )
                }
                Result.Loading -> Unit
            }
        }
    }

    private fun onVerifyButtonClicked() {
        val state = _uiState.value as? MyLoginUiState.VerifySmsCodeUiState ?: return
        _uiState.update { MyLoginUiState.WaitSmsCodeUiState }
        scope.launch {
            when (val result = verifySmsCodeByPhoneNumber(
                phoneNumber = PhoneNumberModel(currentPhone),
                smsCode = SmsCodeModel(state.smsCode)
            )) {
                is Result.Success -> { /* навигация обрабатывается снаружи */ }
                is Result.Error -> _uiState.update {
                    MyLoginUiState.ErrorUiState(
                        errorMessage = result.exception.message ?: "Ошибка входа",
                        cause = result.exception as? AuthException
                    )
                }
                Result.Loading -> Unit
            }
        }
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }

    companion object {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
                return MyLoginViewModel(
                    sendSmsCodeByPhoneNumber = object : SendSmsCodeByPhoneNumberUseCase {
                        override suspend fun invoke(phoneNumber: PhoneNumberModel) =
                            Result.Success(AuthStatus.WaitSms)
                    },
                    verifySmsCodeByPhoneNumber = object : VerifySmsCodeByPhoneNumberUseCase {
                        override suspend fun invoke(phoneNumber: PhoneNumberModel, smsCode: SmsCodeModel) =
                            Result.Success(false)
                    },
                    savedStateHandle = SavedStateHandle()
                ) as T
            }
        }
    }
}
