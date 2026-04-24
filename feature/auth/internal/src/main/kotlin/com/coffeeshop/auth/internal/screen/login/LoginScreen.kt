package com.coffeeshop.auth.internal.screen.login

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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arttttt.nav3router.Router
import com.coffeeshop.auth.api.presentation.navigation.RegisterRoute
import com.coffeeshop.designsystem.Secondary
import com.coffeeshop.designsystem.White
import com.coffeeshop.designsystem.components.CoffeeButton
import com.coffeeshop.designsystem.components.CoffeeInputField
import com.coffeeshop.designsystem.components.LoadingOverlay
import com.coffeeshop.designsystem.components.SimpleTopBar
import com.coffeshop.catalog.api.presentation.navigation.CatalogRoute
import com.coffeshop.navigation.Route

@Composable
fun LoginScreen(
    router: Router<Route>
) = LoginScreenInternal(
    router = router
)

@Composable
internal fun LoginScreenInternal(
    router: Router<Route>,
    viewModelFactory: ViewModelProvider.Factory = LoginViewModel.previewFactory,
    viewModel: LoginViewModel = viewModel(factory = viewModelFactory)
) {
    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigateToHome.collect {
            router.replaceCurrent(CatalogRoute(isLoggedIn = true))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LoginContent(
            state = uiState.value,
            onEvent = viewModel::reduce,
            onNavigateToRegister = {
                if (viewModel.isNavigateWithPhoneAble(viewModel.currentPhone)) {
                    router.push(RegisterRoute(
                        phone = viewModel.currentPhone
                    ))
                }
                else {
                    router.push(RegisterRoute())
                }
            }
        )
        if (uiState.value.isLoading) {
            LoadingOverlay()
        }
    }
}

private val LoginUiState.isLoading: Boolean
    get() = when (this) {
        is LoginUiState.InputPhone -> isLoading
        is LoginUiState.EnteringCode -> isLoading
    }

@Composable
private fun LoginContent(
    state: LoginUiState,
    onEvent: (LoginUiStateEvent) -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .systemBarsPadding()
            .imePadding()
    ) {
        SimpleTopBar(title = "Вход")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onNavigateToRegister) {
                Text(text = "Нет аккаунта?", color = Secondary)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val phone = when (state) {
                is LoginUiState.InputPhone -> state.phone
                is LoginUiState.EnteringCode -> state.phone
            }
            val smsSent = state is LoginUiState.EnteringCode

            CoffeeInputField(
                label = "Номер телефона",
                value = phone,
                onValueChange = { newValue ->
                    if (!smsSent) {
                        val digits = newValue.filter { it.isDigit() }.take(10)
                        onEvent(LoginUiStateEvent.PhoneChanged(digits))
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
                val codeState = state as? LoginUiState.EnteringCode
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CoffeeInputField(
                        label = "Код из смс",
                        value = codeState?.smsCode ?: "",
                        onValueChange = { newValue ->
                            val digits = newValue.filter { it.isDigit() }.take(6)
                            onEvent(LoginUiStateEvent.SmsCodeChanged(digits))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardType = KeyboardType.Number,
                        isError = codeState?.isCodeError == true,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (codeState?.resendEnabled == true) {
                        TextButton(
                            onClick = { onEvent(LoginUiStateEvent.ResendSmsClicked) },
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

        if (state is LoginUiState.InputPhone) {
            CoffeeButton(
                text = "Отправить смс",
                onClick = { onEvent(LoginUiStateEvent.SendSmsClicked) },
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

@Preview
@Composable
fun LoginScreenPreview() = LoginScreen(router = Router())
