package com.coffeeshop.common.model.order

import com.coffeeshop.common.model.user.User
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.order.OrderStatus
import com.coffeeshop.common.model.support.Price
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Order(
    val orderId: ID,
    val user: User,
    val items: List<OrderItem>,
    val orderStatus: OrderStatus,
    val comment: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {

    val totalPrice: Price = items.fold(Price(0, 0)) { acc, item ->
        acc + item.totalPrice
    }
}