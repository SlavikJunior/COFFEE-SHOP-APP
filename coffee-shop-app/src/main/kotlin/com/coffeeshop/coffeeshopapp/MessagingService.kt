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
import com.coffeeshop.common.events.OrderEventBus
import com.coffeeshop.common.events.OrderStatusUpdate
import com.coffeeshop.common.model.order.OrderStatus
import com.coffeeshop.di.qualifiers.ApplicationContext
import com.coffeeshop.network.NotificationsRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessagingService : FirebaseMessagingService() {

    private lateinit var notificationsRepository: NotificationsRepository
    private val notificationShower by lazy { NotificationShower(applicationContext) }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        notificationsRepository = (applicationContext as CoffeeShopApp).networkComponent.notificationsRepository
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken: $token")
        scope.launch {
            notificationsRepository.registerToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "onMessageReceived data: ${message.data}")
        notificationShower.show(message)

        val type = message.data[TYPE_KEY] ?: return
        when (type) {
            TYPE_ORDER_STATUS -> {
                val orderId = message.data[ORDER_ID_KEY]?.toLongOrNull() ?: return
                val status = message.data[STATUS_KEY]
                    ?.let { status: String -> runCatching { OrderStatus.entries.find { it.name.equals(status, ignoreCase = true) } }.getOrNull() } ?: return
                OrderEventBus.notifyOrderStatusChanged(OrderStatusUpdate(orderId, status))
            }
            TYPE_CHAT_MESSAGE -> Unit
            TYPE_CUSTOM -> Unit
        }
    }

    private companion object {
        const val TAG = "MessagingService"
        const val TYPE_KEY = "type"
        const val ORDER_ID_KEY = "orderId"
        const val STATUS_KEY = "status"
        const val TYPE_ORDER_STATUS = "ORDER_STATUS"
        const val TYPE_CHAT_MESSAGE = "CHAT_MESSAGE"
        const val TYPE_CUSTOM = "CUSTOM"
    }
}

internal class NotificationShower
@Inject constructor(
    @field:ApplicationContext private val context: Context
) {

    fun show(message: RemoteMessage) {
        val type = message.data[TYPE_KEY] ?: return
        val title = message.notification?.title ?: message.data[TITLE_KEY] ?: return
        val body = message.notification?.body ?: message.data[MESSAGE_KEY] ?: return

        val channelId = when (type) {
            TYPE_PROMO -> CoffeeShopApp.CHANNEL_PROMO
            TYPE_AUTH -> CoffeeShopApp.CHANNEL_AUTH
            TYPE_ORDER_STATUS -> CoffeeShopApp.CHANNEL_ORDER_STATUS
            else -> CoffeeShopApp.CHANNEL_DEFAULT
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val priority = if (type == TYPE_AUTH) NotificationCompat.PRIORITY_HIGH
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
        const val TYPE_KEY = "type"
        const val TITLE_KEY = "title"
        const val MESSAGE_KEY = "message"
        const val TYPE_PROMO = "CUSTOM"
        const val TYPE_AUTH = "AUTH"
        const val TYPE_ORDER_STATUS = "ORDER_STATUS"
        const val TAG = "NotificationBuilder"
    }
}
