package com.coffeeshop.common.events

import com.coffeeshop.common.model.order.OrderStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class OrderStatusUpdate(val orderId: Long, val status: OrderStatus)

object OrderEventBus {
    private val _orderStatusChanged = MutableSharedFlow<OrderStatusUpdate>(extraBufferCapacity = 10)
    val orderStatusChanged: SharedFlow<OrderStatusUpdate> = _orderStatusChanged.asSharedFlow()

    fun notifyOrderStatusChanged(update: OrderStatusUpdate) {
        _orderStatusChanged.tryEmit(update)
    }
}
