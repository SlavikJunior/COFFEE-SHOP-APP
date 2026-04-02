package com.coffeeshop.auth.internal.screen.register

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
fun RegisterScreen(onNavigateToLogin: () -> Unit = {}) =
    RegisterScreenInternal(onNavigateToLogin = onNavigateToLogin)

@Composable
private fun RegisterScreenInternal(
    onNavigateToLogin: () -> Unit,
    viewModel: MyRegisterViewModel = viewModel(factory = MyRegisterViewModel.Companion.factory)
) {
    val uiState = viewModel.uiState.collectAsState()

    when (val state = uiState.value) {
        is MyRegisterUiState.InputDataUiState -> InputDataState(
            state = state,
            onEvent = viewModel::reduce,
            onNavigateToLogin = onNavigateToLogin
        )
        is MyRegisterUiState.VerifySmsCodeUiState -> VerifyCodeState(
            state = state,
            onEvent = viewModel::reduce
        )
        is MyRegisterUiState.ErrorUiState -> ErrorState(
            state = state,
            onRetry = { viewModel.reduce(RegisterUiStateEvent.SendSmsButtonClicked) }
        )
        MyRegisterUiState.WaitSmsCodeUiState -> LoadingState()
    }
}

@Composable
private fun InputDataState(
    state: MyRegisterUiState.InputDataUiState,
    onEvent: (RegisterUiStateEvent) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        ProfileTopBar(
            title = "Регистрация",
            onCloseClick = {}
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Уже есть аккаунт?",
                    color = Secondary
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CoffeeInputField(
                label = "Имя",
                value = state.name,
                onValueChange = { onEvent(RegisterUiStateEvent.NameChangeEvent(it)) },
                modifier = Modifier.fillMaxWidth(),
            )
            CoffeeInputField(
                label = "Номер телефона",
                value = state.phone,
                onValueChange = { newValue ->
                    val digits = newValue.removePrefix("+7").filter { it.isDigit() }.take(10)
                    onEvent(RegisterUiStateEvent.PhoneChangeEvent("+7$digits"))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Number,
            )
        }
        CoffeeButton(
            text = "Отправить смс",
            onClick = { onEvent(RegisterUiStateEvent.SendSmsButtonClicked) },
            enabled = state.sendButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )
    }
}

@Composable
private fun VerifyCodeState(
    state: MyRegisterUiState.VerifySmsCodeUiState,
    onEvent: (RegisterUiStateEvent) -> Unit,
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
                    onEvent(RegisterUiStateEvent.SmsCodeChangeEvent(digits))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Number,
            )
        }
        CoffeeButton(
            text = "Подтвердить",
            onClick = { onEvent(RegisterUiStateEvent.VerifyButtonClicked) },
            enabled = state.verifyButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )
    }
}

@Composable
private fun ErrorState(
    state: MyRegisterUiState.ErrorUiState,
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
fun RegisterScreenPreview() = RegisterScreen()