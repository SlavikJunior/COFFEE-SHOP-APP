package com.coffeeshop.coffeeshopapp.permissions

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class NotificationsPermissionStatus {
    Granted, NeedsRequest, NeedsRationale, PermanentlyDenied
}

class NotificationsPermissionState {
    private val _status = MutableStateFlow(NotificationsPermissionStatus.Granted)
    val status = _status.asStateFlow()

    private var requestCount = 0

    suspend fun refresh(context: Context) {
        val newStatus = calculateStatus(context)
        _status.emit(newStatus)
    }

    fun incrementRequestCount() {
        requestCount++
    }

    private fun calculateStatus(context: Context): NotificationsPermissionStatus {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return NotificationsPermissionStatus.Granted
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS
        val granted = ContextCompat.checkSelfPermission(context, permission) ==
                PermissionChecker.PERMISSION_GRANTED

        return when {
            granted -> NotificationsPermissionStatus.Granted
            requestCount == 0 -> NotificationsPermissionStatus.NeedsRequest
            else -> {
                val shouldShow = context.shouldShowRequestPermissionRationale(permission)
                if (shouldShow) NotificationsPermissionStatus.NeedsRationale
                else NotificationsPermissionStatus.PermanentlyDenied
            }
        }
    }

    private fun Context.shouldShowRequestPermissionRationale(permission: String): Boolean {
        return try {
            androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(
                this as android.app.Activity,
                permission
            )
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        @Volatile private var instance: NotificationsPermissionState? = null

        fun getInstance(): NotificationsPermissionState =
            instance ?: synchronized(this) {
                instance ?: NotificationsPermissionState().also { instance = it }
            }
    }
}

fun Context.notificationsPermissionState() = NotificationsPermissionState.getInstance()
