package com.coffeeshop.profile.internal.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.coffeeshop.designsystem.common.Beige
import com.coffeeshop.profile.internal.R
import com.coffeeshop.designsystem.common.Secondary
import com.coffeeshop.designsystem.common.White
import com.coffeeshop.designsystem.components.CoffeeButton
import com.coffeeshop.designsystem.components.CoffeeProfileField
import com.coffeeshop.designsystem.components.CoffeeToggleRow
import com.coffeeshop.designsystem.components.ProfileLinkRow
import com.coffeeshop.designsystem.components.ProfileTopBar

@Composable
internal fun ProfileScreen(viewModelFactory: ViewModelProvider.Factory) {
    val viewModel = viewModel<ProfileViewModel>(factory = viewModelFactory)
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val message = uiState.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.reduce(ProfileUiEvent.DismissSnackbar)
    }

    ProfileScreenContent(
        snackbarHostState,
        uiState,
        onEvent = { event -> viewModel.reduce(event) }
    )
}

@Composable
private fun ProfileScreenContent(
    snackbarHostState: SnackbarHostState,
    uiState: ProfileUiState,
    onEvent: (ProfileUiEvent) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Beige,
        topBar = {
            ProfileTopBar(
                title = stringResource(R.string.profile_top_bar_title),
                onCloseClick = { onEvent(ProfileUiEvent.NavigateBack) },
                modifier = Modifier
                    .statusBarsPadding()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {


            Spacer(modifier = Modifier.height(24.dp))

            ProfileInfoSection(
                name = uiState.name,
                phone = uiState.phoneNumber,
                email = uiState.email,
            )

            Spacer(modifier = Modifier.height(8.dp))

            CoffeeToggleRow(
                label = stringResource(R.string.profile_notifications_label),
                checked = false,
                onCheckedChange = { onEvent(ProfileUiEvent.NotificationsStub) },
            )

            ProfileLinkRow(
                label = stringResource(R.string.profile_order_history_label),
                onClick = { onEvent(ProfileUiEvent.OpenOrderHistory) },
            )

            ProfileLinkRow(
                label = stringResource(R.string.profile_feedback_label),
                onClick = { onEvent(ProfileUiEvent.FeedbackStub) },
            )

            Spacer(modifier = Modifier.height(32.dp))

            CoffeeButton(
                text = stringResource(R.string.profile_logout_button),
                onClick = { onEvent(ProfileUiEvent.Logout) },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AvatarPlaceholder() {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(White),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = Secondary,
            modifier = Modifier.size(32.dp),
        )
    }
}

@Composable
private fun ProfileInfoSection(name: String, phone: String, email: String) {
    CoffeeProfileField(
        firstLabel = stringResource(R.string.profile_field_name),
        secondLabel = name,
        modifier = Modifier.fillMaxWidth(),
    )
    CoffeeProfileField(
        firstLabel = stringResource(R.string.profile_field_phone),
        secondLabel = phone,
        modifier = Modifier.fillMaxWidth(),
    )
    CoffeeProfileField(
        firstLabel = stringResource(R.string.profile_field_email),
        secondLabel = email,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
@Preview
private fun ProfileScreenContentPreview() = ProfileScreenContent(
    snackbarHostState = SnackbarHostState(),
    uiState = ProfileUiState(
        status = ProfileUiStateStatus.Success,
        name = "Вячеслав",
        phoneNumber = "+79120530904",
        email = "Пока не добавлен",
        snackbarMessage = null
    ),
    onEvent = {}
)