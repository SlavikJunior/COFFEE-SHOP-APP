package com.coffeeshop.common.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object OrderEventBus {
    private val _orderStatusChanged = MutableSharedFlow<Unit>(extraBufferCapacity = 10)
    val orderStatusChanged: SharedFlow<Unit> = _orderStatusChanged.asSharedFlow()

    fun notifyOrderStatusChanged() {
        _orderStatusChanged.tryEmit(Unit)
    }
}
