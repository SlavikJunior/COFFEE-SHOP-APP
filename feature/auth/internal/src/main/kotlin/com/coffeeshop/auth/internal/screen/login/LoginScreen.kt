package com.coffeeshop.auth.internal.screen.login

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arttttt.nav3router.Router
import com.coffeeshop.auth.api.presentation.navigation.RegisterRoute
import com.coffeeshop.auth.internal.R
import com.coffeeshop.designsystem.common.Secondary
import com.coffeeshop.designsystem.common.White
import com.coffeeshop.designsystem.components.CoffeeButton
import com.coffeeshop.designsystem.components.CoffeeInputField
import com.coffeeshop.designsystem.components.LoadingOverlay
import com.coffeeshop.designsystem.components.SimpleTopBar
import com.coffeeshop.di.qualifiers.LoginViewModelFactory
import com.coffeshop.catalog.api.presentation.navigation.CatalogRoute
import com.coffeshop.navigation.Route

@Composable
fun LoginScreen(
    router: Router<Route>,
    @LoginViewModelFactory viewModelFactory: ViewModelProvider.Factory,
    message: String? = null
) = LoginScreenInternal(
    router = router, viewModelFactory, message
)

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
internal fun LoginScreenInternal(
    router: Router<Route>,
    @LoginViewModelFactory viewModelFactory: ViewModelProvider.Factory,
    message: String? = null
) {
    val viewModel = viewModel<LoginViewModel>(factory = viewModelFactory)
    val uiState = viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.navigateToHome.collect {
            router.replaceStack(CatalogRoute(isLoggedIn = true))
        }
    }

    message?.let { message ->
        LaunchedEffect(Unit) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.error.collect { error ->
            val msg = when (error) {
                LoginError.SmsSendFailed -> context.getString(R.string.error_sms_send_failed)
                LoginError.WrongCode -> context.getString(R.string.error_wrong_code)
                LoginError.AccountNotFound -> context.getString(R.string.error_account_not_found)
                LoginError.ServerError -> context.getString(R.string.error_server)
                LoginError.NetworkError -> context.getString(R.string.error_network)
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
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

@SuppressLint("ContextCastToActivity")
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
        SimpleTopBar(title = stringResource(R.string.login_top_bar_text))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onNavigateToRegister) {
                Text(text = stringResource(R.string.no_account_text_button), color = Secondary)
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
                label = stringResource(R.string.phone_number_text),
                value = phone,
                onValueChange = { newValue ->
                    if (!smsSent) {
                        val digits = newValue.filter { it.isDigit() }.take(10)
                        onEvent(LoginUiStateEvent.PhoneChanged(digits))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Number,
                prefix = stringResource(R.string.russian_phone_number_prefix),
            )

            AnimatedVisibility(
                visible = smsSent,
                enter = fadeIn() + expandVertically()
            ) {
                val codeState = state as? LoginUiState.EnteringCode
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CoffeeInputField(
                        label = stringResource(R.string.sms_code_text),
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
                        val activity = LocalContext.current as Activity

                        TextButton(
                            onClick = { onEvent(LoginUiStateEvent.ResendSmsClicked(
                                activity = activity
                            )) },
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Text(text = stringResource(R.string.send_again_text), color = Secondary, fontSize = 13.sp)
                        }
                    } else {
                        Text(
                            text = stringResource(
                                R.string.send_again_after_text,
                                codeState?.timerSeconds ?: 60
                            ),
                            color = Secondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        if (state is LoginUiState.InputPhone) {
            val activity = LocalContext.current as Activity

            CoffeeButton(
                text = stringResource(R.string.send_sms_text),
                onClick = { onEvent(LoginUiStateEvent.SendSmsClicked(
                    activity = activity
                )) },
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