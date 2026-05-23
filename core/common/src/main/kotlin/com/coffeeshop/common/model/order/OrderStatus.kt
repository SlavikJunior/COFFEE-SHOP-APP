package com.coffeeshop.common.model.order

enum class OrderStatus {
    PENDING,    // создан, ожидает оплаты
    PAID,       // оплачен, принят
    PREPARING,  // готовится
    READY,      // готов, ждёт выдачи
    COMPLETED,  // выдан
    CANCELLED   // отменён
}