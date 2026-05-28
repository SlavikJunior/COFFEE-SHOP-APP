package com.coffeeshop.coffeeshopapp.permissions

import android.Manifest
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun NotificationsPermissionGate(
    permissionLauncher: ActivityResultLauncher<String>,
    content: @Composable () -> Unit
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        content()
        return
    }

    val context = LocalContext.current
    val state = context.notificationsPermissionState()
    val status by state.status.collectAsState()

    LaunchedEffect(status) {
        when (status) {
            NotificationsPermissionStatus.NeedsRequest -> {
                state.incrementRequestCount()
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            NotificationsPermissionStatus.NeedsRationale -> {
                state.incrementRequestCount()
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            else -> Unit
        }
    }

    when (status) {
        NotificationsPermissionStatus.Granted,
        NotificationsPermissionStatus.NeedsRequest,
        NotificationsPermissionStatus.NeedsRationale -> {
            content()
        }
        NotificationsPermissionStatus.PermanentlyDenied -> {
            content()
            BlockingPermissionSheet()
        }
    }
}
