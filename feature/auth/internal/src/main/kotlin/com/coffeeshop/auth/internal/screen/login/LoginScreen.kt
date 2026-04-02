package com.coffeeshop.auth.internal.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coffeeshop.designsystem.DarkBrown
import com.coffeeshop.designsystem.Secondary
import com.coffeeshop.designsystem.White
import com.coffeeshop.designsystem.components.CoffeeButton
import com.coffeeshop.designsystem.components.CoffeeInputField
import com.coffeeshop.designsystem.components.ProfileTopBar

@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit = {}) =
    LoginScreenInternal(onNavigateToRegister = onNavigateToRegister)

@Composable
internal fun LoginScreenInternal(
    onNavigateToRegister: () -> Unit,
    viewModel: MyLoginViewModel = viewModel(factory = MyLoginViewModel.Companion.factory)
) {
    val uiState = viewModel.uiState.collectAsState()

    when (val state = uiState.value) {
        is MyLoginUiState.InputPhoneUiState -> InputPhoneState(
            state = state,
            onEvent = viewModel::reduce,
            onNavigateToRegister = onNavigateToRegister
        )
        is MyLoginUiState.VerifySmsCodeUiState -> VerifyCodeState(
            state = state,
            onEvent = viewModel::reduce
        )
        is MyLoginUiState.ErrorUiState -> ErrorState(
            state = state,
            onRetry = { viewModel.reduce(LoginUiStateEvent.SendSmsButtonClicked) }
        )
        MyLoginUiState.WaitSmsCodeUiState -> LoadingState()
    }
}

@Composable
private fun InputPhoneState(
    state: MyLoginUiState.InputPhoneUiState,
    onEvent: (LoginUiStateEvent) -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        ProfileTopBar(
            title = "Вход",
            onCloseClick = {}
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "Нет аккаунта?",
                    color = Secondary
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            CoffeeInputField(
                label = "Номер телефона",
                value = state.phone,
                onValueChange = { newValue ->
                    val digits = newValue.removePrefix("+7").filter { it.isDigit() }.take(10)
                    onEvent(LoginUiStateEvent.PhoneChangeEvent("+7$digits"))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Number,
            )
        }
        CoffeeButton(
            text = "Отправить смс",
            onClick = { onEvent(LoginUiStateEvent.SendSmsButtonClicked) },
            enabled = state.sendButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )
    }
}

@Composable
private fun VerifyCodeState(
    state: MyLoginUiState.VerifySmsCodeUiState,
    onEvent: (LoginUiStateEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        ProfileTopBar(
            title = "Подтверждение",
            onCloseClick = {}
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            CoffeeInputField(
                label = "Код из смс",
                value = state.smsCode,
                onValueChange = { newValue ->
                    val digits = newValue.filter { it.isDigit() }.take(6)
                    onEvent(LoginUiStateEvent.SmsCodeChangeEvent(digits))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Number,
            )
        }
        CoffeeButton(
            text = "Войти",
            onClick = { onEvent(LoginUiStateEvent.VerifyButtonClicked) },
            enabled = state.verifyButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )
    }
}

@Composable
private fun ErrorState(
    state: MyLoginUiState.ErrorUiState,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = state.errorMessage.ifEmpty { "Произошла ошибка" },
            color = DarkBrown,
            modifier = Modifier.padding(16.dp)
        )
        CoffeeButton(
            text = "Попробовать снова",
            onClick = onRetry,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(64.dp))
    }
}

@Preview
@Composable
fun LoginScreenPreview() = LoginScreen()
