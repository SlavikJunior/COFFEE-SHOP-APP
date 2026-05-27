package com.coffeeshop.coffeeshopapp

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.coffeeshop.auth.api.domain.usecase.DeleteTokenUseCase
import com.coffeeshop.auth.api.domain.usecase.SendNewTokenUseCase
import com.coffeeshop.common.events.OrderEventBus
import com.coffeeshop.di.qualifiers.ApplicationContext
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject

class MessagingService : FirebaseMessagingService() {

    @Inject internal lateinit var sendNewToken: SendNewTokenUseCase
    @Inject internal lateinit var deleteToken: DeleteTokenUseCase

    private val notificationShower by lazy { NotificationShower(applicationContext) }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "onMessageReceived data: ${message.data}")
        notificationShower.show(message)
        if (message.data[KIND_KEY] == KIND_ORDER_STATUS) {
            OrderEventBus.notifyOrderStatusChanged()
        }
    }

    private companion object {
        const val TAG = "MessagingService"
        const val KIND_KEY = "kind"
        const val KIND_ORDER_STATUS = "order_status"
    }
}

internal class NotificationShower
@Inject constructor(
    @field:ApplicationContext private val context: Context
) {

    fun show(message: RemoteMessage) {
        val kind = message.data[KIND_KEY] ?: return
        val title = message.data[TITLE_KEY] ?: return
        val body = message.data[MESSAGE_KEY] ?: return

        val channelId = when (kind) {
            KIND_PROMO -> CoffeeShopApp.CHANNEL_PROMO
            KIND_AUTH -> CoffeeShopApp.CHANNEL_AUTH
            KIND_ORDER_STATUS -> CoffeeShopApp.CHANNEL_ORDER_STATUS
            else -> CoffeeShopApp.CHANNEL_DEFAULT
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val priority = if (kind == KIND_AUTH) NotificationCompat.PRIORITY_HIGH
        else NotificationCompat.PRIORITY_DEFAULT

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        } else true
        if (hasPermission) {
            NotificationManagerCompat.from(context)
                .notify(System.currentTimeMillis().toInt(), notification)
        }
    }

    private companion object {
        const val KIND_KEY = "kind"
        const val TITLE_KEY = "title"
        const val MESSAGE_KEY = "message"
        const val KIND_PROMO = "promo"
        const val KIND_AUTH = "auth"
        const val KIND_ORDER_STATUS = "order_status"
        const val TAG = "NotificationBuilder"
    }
}
