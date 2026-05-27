package com.coffeeshop.activeorders.api.domain.model

import com.coffeeshop.common.model.order.OrderStatus

data class ActiveOrder(
    val id: Long,
    val items: List<ActiveOrderItem>,
    val totalAmount: String,
    val comment: String?,
    val createdAt: String,
    val status: OrderStatus,
)

data class ActiveOrderItem(
    val name: String,
    val quantity: Int,
)
