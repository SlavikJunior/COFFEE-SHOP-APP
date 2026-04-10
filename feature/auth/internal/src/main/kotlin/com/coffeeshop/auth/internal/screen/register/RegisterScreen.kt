package com.coffeeshop.auth.internal.screen.register

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.arttttt.nav3router.Router
import com.coffeeshop.auth.api.presentation.navigation.LoginRoute
import com.coffeeshop.designsystem.DarkBrown
import com.coffeeshop.designsystem.Secondary
import com.coffeeshop.designsystem.White
import com.coffeeshop.designsystem.components.CoffeeButton
import com.coffeeshop.designsystem.components.CoffeeInputField
import com.coffeeshop.designsystem.components.SimpleTopBar
import com.coffeshop.navigation.Route
import com.coffeshop.products.api.presentation.navigation.ProductsRoute

@Composable
fun RegisterScreen(
    router: Router<Route>,
) = RegisterScreenInternal(router = router)

@Composable
private fun RegisterScreenInternal(
    router: Router<Route>,
    viewModelFactory: ViewModelProvider.Factory = RegisterViewModel.previewFactory,
    viewModel: RegisterViewModel = viewModel(factory = viewModelFactory)
) {
    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigateToHome.collect {
            router.replaceCurrent(ProductsRoute(isLoggedIn = true))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        RegisterContent(
            state = uiState.value,
            onEvent = viewModel::reduce,
            onNavigateToLogin = {
                if (viewModel.isNavigateToLoginWithPhoneValid(viewModel.currentPhone)) {
                    router.push(LoginRoute(
                        phone = viewModel.currentPhone
                    ))
                }
                else {
                    router.push(LoginRoute())
                }
            }
        )
        if (uiState.value.isLoading) {
            LoadingOverlay()
        }
    }
}

private val RegisterUiState.isLoading: Boolean
    get() = when (this) {
        is RegisterUiState.InputData -> isLoading
        is RegisterUiState.EnteringCode -> isLoading
    }

@Composable
private fun RegisterContent(
    state: RegisterUiState,
    onEvent: (RegisterUiStateEvent) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .systemBarsPadding()
            .imePadding()
    ) {
        SimpleTopBar(title = "Регистрация")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onNavigateToLogin) {
                Text(text = "Уже есть аккаунт?", color = Secondary)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val name = when (state) {
                is RegisterUiState.InputData -> state.name
                is RegisterUiState.EnteringCode -> state.name
            }
            val phone = when (state) {
                is RegisterUiState.InputData -> state.phone
                is RegisterUiState.EnteringCode -> state.phone
            }
            val smsSent = state is RegisterUiState.EnteringCode

            CoffeeInputField(
                label = "Имя",
                value = name,
                onValueChange = { if (!smsSent) onEvent(RegisterUiStateEvent.NameChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
            )
            CoffeeInputField(
                label = "Номер телефона",
                value = phone,
                onValueChange = { newValue ->
                    if (!smsSent) {
                        val digits = newValue.filter { it.isDigit() }.take(10)
                        onEvent(RegisterUiStateEvent.PhoneChanged(digits))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Number,
                prefix = "+7",
            )

            AnimatedVisibility(
                visible = smsSent,
                enter = fadeIn() + expandVertically()
            ) {
                val codeState = state as? RegisterUiState.EnteringCode
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CoffeeInputField(
                        label = "Код из смс",
                        value = codeState?.smsCode ?: "",
                        onValueChange = { newValue ->
                            val digits = newValue.filter { it.isDigit() }.take(6)
                            onEvent(RegisterUiStateEvent.SmsCodeChanged(digits))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardType = KeyboardType.Number,
                        isError = codeState?.isCodeError == true,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (codeState?.resendEnabled == true) {
                        TextButton(
                            onClick = { onEvent(RegisterUiStateEvent.ResendSmsClicked) },
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Text(text = "Отправить снова", color = Secondary, fontSize = 13.sp)
                        }
                    } else {
                        Text(
                            text = "Повторная отправка через ${codeState?.timerSeconds ?: 60} с",
                            color = Secondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        if (state is RegisterUiState.InputData) {
            CoffeeButton(
                text = "Отправить смс",
                onClick = { onEvent(RegisterUiStateEvent.SendSmsClicked) },
                enabled = state.sendButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(56.dp),
            color = DarkBrown
        )
    }
}

@Preview
@Composable
fun RegisterScreenPreview() = RegisterScreen(
    router = Router()
)