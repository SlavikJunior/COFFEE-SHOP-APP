package com.coffeeshop.auth.internal.screen.register

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
import com.coffeeshop.auth.api.presentation.navigation.LoginRoute
import com.coffeeshop.auth.internal.R
import com.coffeeshop.designsystem.common.Secondary
import com.coffeeshop.designsystem.common.White
import com.coffeeshop.designsystem.components.CoffeeButton
import com.coffeeshop.designsystem.components.CoffeeInputField
import com.coffeeshop.designsystem.components.LoadingOverlay
import com.coffeeshop.designsystem.components.SimpleTopBar
import com.coffeeshop.di.qualifiers.RegisterViewModelFactory
import com.coffeshop.catalog.api.presentation.navigation.CatalogRoute
import com.coffeshop.navigation.Route

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
internal fun RegisterScreen(
    router: Router<Route>,
    @RegisterViewModelFactory viewModelFactory: ViewModelProvider.Factory
) {
    val viewModel = viewModel<RegisterViewModel>(factory = viewModelFactory)
    val uiState = viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.navigateToHome.collect {
            router.replaceStack(CatalogRoute(isLoggedIn = true))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.error.collect { error ->
            val msg = when (error) {
                RegisterError.SmsSendFailed -> context.getString(R.string.error_sms_send_failed)
                RegisterError.WrongCode -> context.getString(R.string.error_wrong_code)
                RegisterError.AlreadyRegistered -> context.getString(R.string.error_phone_already_registered)
                RegisterError.ServerError -> context.getString(R.string.error_server)
                RegisterError.NetworkError -> context.getString(R.string.error_network)
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        RegisterContent(
            state = uiState.value,
            onEvent = viewModel::reduce,
            onNavigateToLogin = {
                if (viewModel.isNavigateWithPhoneAble(viewModel.currentPhone)) {
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

@SuppressLint("ContextCastToActivity")
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
        SimpleTopBar(title = stringResource(R.string.register_top_bar_text))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onNavigateToLogin) {
                Text(text = stringResource(R.string.already_has_account_text), color = Secondary)
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
                label = stringResource(R.string.name_text_field),
                value = name,
                onValueChange = { if (!smsSent) onEvent(RegisterUiStateEvent.NameChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
            )
            CoffeeInputField(
                label = stringResource(R.string.phone_number_text_field),
                value = phone,
                onValueChange = { newValue ->
                    if (!smsSent) {
                        val digits = newValue.filter { it.isDigit() }.take(10)
                        onEvent(RegisterUiStateEvent.PhoneChanged(digits))
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
                val codeState = state as? RegisterUiState.EnteringCode
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CoffeeInputField(
                        label = stringResource(R.string.sms_code_text),
                        value = codeState?.smsCode.orEmpty(),
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
                        val activity = LocalContext.current as Activity

                        TextButton(
                            onClick = { onEvent(RegisterUiStateEvent.ResendSmsClicked(activity = activity)) },
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Text(text = stringResource(R.string.send_again_text), color = Secondary, fontSize = 13.sp)
                        }
                    } else {
                        Text(
                            text =  stringResource(R.string.send_again_after_text, codeState?.timerSeconds ?: 60),
                            color = Secondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        if (state is RegisterUiState.InputData) {
            val activity = LocalContext.current as Activity

            CoffeeButton(
                text = stringResource(R.string.send_sms_text),
                onClick = { onEvent(RegisterUiStateEvent.SendSmsClicked(activity = activity)) },
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