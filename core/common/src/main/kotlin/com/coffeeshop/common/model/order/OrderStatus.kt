package com.coffeeshop.common.model.order

enum class OrderStatus {
    PENDING,       // создан, ждёт подтверждения
    CONFIRMED,     // принят баристой
    IN_PROGRESS,   // готовится
    READY,         // готов, ждёт выдачи
    COMPLETED,     // выдан
    CANCELLED     // отменён
}