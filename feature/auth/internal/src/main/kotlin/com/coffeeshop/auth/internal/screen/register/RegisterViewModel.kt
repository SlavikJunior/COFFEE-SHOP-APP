package com.coffeeshop.auth.internal.screen.register

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.coffeeshop.auth.api.domain.exception.AuthException
import com.coffeeshop.auth.api.domain.usecase.RegisterByPhoneNumberAndNameAndSmsCodeUseCase
import com.coffeeshop.auth.api.domain.usecase.SendSmsCodeByPhoneNumberUseCase
import com.coffeeshop.auth.api.domain.usecase.VerifySmsCodeByPhoneNumberUseCase
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.model.AuthStatus
import com.coffeeshop.common.model.NameModel
import com.coffeeshop.common.model.PhoneNumberModel
import com.coffeeshop.common.model.SmsCodeModel
import com.coffeshop.utils.validateName
import com.coffeshop.utils.validateRussianPhoneNumberBy_E_164
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * что должно быть на экране регистрации?
 * поле для ввода телефона -> проверяем что телефон введён верно -> нажимает кнопку отправить смс ->
 * вылезает окно для ввода кода -> проверка введённого кода, если верный код и телефон не был зареган то даём ввести имя и пускаем дальше
 * состояния: ввод данных, ждёт смс, проверка смс, ошибка
 * */

internal sealed interface MyRegisterUiState {

    data object WaitSmsCodeUiState : MyRegisterUiState

    data class VerifySmsCodeUiState(
        val smsCode: String = "",
        val verifyButtonEnabled: Boolean = false
    ) : MyRegisterUiState

    data class InputDataUiState(
        val name: String = "",
        val phone: String = "+7",
        val sendButtonEnabled: Boolean = false
    ) : MyRegisterUiState

    data class ErrorUiState(
        val errorMessage: String = "",
        val cause: AuthException? = null
    ) : MyRegisterUiState
}

internal sealed interface RegisterUiStateEvent {
    data class NameChangeEvent(val name: String) : RegisterUiStateEvent
    data class PhoneChangeEvent(val phone: String) : RegisterUiStateEvent
    data class SmsCodeChangeEvent(val smsCode: String) : RegisterUiStateEvent
    data object SendSmsButtonClicked : RegisterUiStateEvent
    data object VerifyButtonClicked : RegisterUiStateEvent
}

internal class MyRegisterViewModel
@Inject constructor(
    private val sendSmsCodeByPhoneNumber: SendSmsCodeByPhoneNumberUseCase,
    private val verifySmsCodeByPhoneNumber: VerifySmsCodeByPhoneNumberUseCase,
    private val registerByPhoneNumberAndNameAndSmsCode: RegisterByPhoneNumberAndNameAndSmsCodeUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val scope = viewModelScope
    private var currentPhone: String = "+7"
    private var currentName: String = ""

    private val _uiState: MutableStateFlow<MyRegisterUiState> =
        MutableStateFlow(MyRegisterUiState.InputDataUiState())
    val uiState: StateFlow<MyRegisterUiState> = _uiState.asStateFlow()

    fun reduce(event: RegisterUiStateEvent) {
        when (event) {
            is RegisterUiStateEvent.NameChangeEvent -> onChangeName(event)
            is RegisterUiStateEvent.PhoneChangeEvent -> onChangePhone(event)
            is RegisterUiStateEvent.SmsCodeChangeEvent -> onSmsCodeChanged(event)
            is RegisterUiStateEvent.SendSmsButtonClicked -> onSendSmsButtonClicked()
            is RegisterUiStateEvent.VerifyButtonClicked -> onVerifyButtonClicked()
        }
    }

    private fun onChangeName(event: RegisterUiStateEvent.NameChangeEvent) {
        currentName = event.name
        _uiState.update {
            MyRegisterUiState.InputDataUiState(
                name = currentName,
                phone = currentPhone,
                sendButtonEnabled = isSendEnabled()
            )
        }
    }

    private fun onChangePhone(event: RegisterUiStateEvent.PhoneChangeEvent) {
        currentPhone = event.phone
        _uiState.update {
            MyRegisterUiState.InputDataUiState(
                name = currentName,
                phone = currentPhone,
                sendButtonEnabled = isSendEnabled()
            )
        }
    }

    private fun onSmsCodeChanged(event: RegisterUiStateEvent.SmsCodeChangeEvent) {
        _uiState.update {
            MyRegisterUiState.VerifySmsCodeUiState(
                smsCode = event.smsCode,
                verifyButtonEnabled = event.smsCode.length == 6
            )
        }
    }

    private fun onSendSmsButtonClicked() {
        if (!isSendEnabled()) {
            _uiState.update { MyRegisterUiState.ErrorUiState(cause = AuthException.InvalidInputDataFormat()) }
            return
        }
        _uiState.update { MyRegisterUiState.WaitSmsCodeUiState }
        scope.launch {
            when (val result = sendSmsCodeByPhoneNumber(PhoneNumberModel(currentPhone))) {
                is Result.Success -> _uiState.update { MyRegisterUiState.VerifySmsCodeUiState() }
                is Result.Error -> _uiState.update {
                    MyRegisterUiState.ErrorUiState(
                        errorMessage = result.exception.message ?: "Ошибка отправки СМС",
                        cause = result.exception as? AuthException
                    )
                }

                Result.Loading -> Unit
            }
        }
    }

    private fun onVerifyButtonClicked() {
        val state = _uiState.value as? MyRegisterUiState.VerifySmsCodeUiState ?: return
        _uiState.update { MyRegisterUiState.WaitSmsCodeUiState }
        scope.launch {
            when (val result = registerByPhoneNumberAndNameAndSmsCode(
                phoneNumber = PhoneNumberModel(currentPhone),
                name = NameModel(currentName),
                smsCode = SmsCodeModel(state.smsCode)
            )) {
                is Result.Success -> { /* навигация обрабатывается снаружи */
                }

                is Result.Error -> _uiState.update {
                    MyRegisterUiState.ErrorUiState(
                        errorMessage = result.exception.message ?: "Ошибка регистрации",
                        cause = result.exception as? AuthException
                    )
                }

                Result.Loading -> Unit
            }
        }
    }

    private fun isSendEnabled(): Boolean =
        validateRussianPhoneNumberBy_E_164(currentPhone) && validateName(currentName)

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }

    companion object {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
                return MyRegisterViewModel(
                    sendSmsCodeByPhoneNumber = object : SendSmsCodeByPhoneNumberUseCase {
                        override suspend fun invoke(phoneNumber: PhoneNumberModel) =
                            Result.Success(AuthStatus.WaitSms)
                    },
                    verifySmsCodeByPhoneNumber = object : VerifySmsCodeByPhoneNumberUseCase {
                        override suspend fun invoke(
                            phoneNumber: PhoneNumberModel,
                            smsCode: SmsCodeModel
                        ) =
                            Result.Success(false)
                    },
                    registerByPhoneNumberAndNameAndSmsCode = object :
                        RegisterByPhoneNumberAndNameAndSmsCodeUseCase {
                        override suspend fun invoke(
                            phoneNumber: PhoneNumberModel,
                            name: NameModel,
                            smsCode: SmsCodeModel
                        ) =
                            Result.Success(AuthStatus.User)
                    },
                    savedStateHandle = SavedStateHandle()
                ) as T
            }
        }

        val newFactory = viewModelFactory {
            initializer {
                MyRegisterViewModel(
                    sendSmsCodeByPhoneNumber = TODO(),
                    verifySmsCodeByPhoneNumber = TODO(),
                    registerByPhoneNumberAndNameAndSmsCode = TODO(),
                    savedStateHandle = SavedStateHandle()
                )
            }
        }
    }
}
