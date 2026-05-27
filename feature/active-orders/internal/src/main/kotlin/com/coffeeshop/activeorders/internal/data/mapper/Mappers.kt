package com.coffeeshop.activeorders.internal.data.mapper

import com.coffeeshop.activeorders.api.domain.model.ActiveOrder
import com.coffeeshop.activeorders.api.domain.model.ActiveOrderItem
import com.coffeeshop.common.model.order.OrderStatus
import com.coffeeshop.contracts.OrderDetailDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
private val outputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale.forLanguageTag("ru"))

internal fun OrderDetailDto.toActiveOrder(): ActiveOrder = ActiveOrder(
    id = id,
    items = items.map { ActiveOrderItem(name = it.menuItemName, quantity = it.quantity) },
    totalAmount = totalPrice,
    comment = comment?.takeIf { it.isNotBlank() },
    createdAt = runCatching {
        LocalDateTime.parse(createdAt, inputFormatter).format(outputFormatter)
    }.getOrDefault(createdAt),
    status = orderStatus.toCommonStatus(),
)

private fun com.coffeeshop.contracts.OrderStatus.toCommonStatus(): OrderStatus = when (this) {
    com.coffeeshop.contracts.OrderStatus.PENDING   -> OrderStatus.PENDING
    com.coffeeshop.contracts.OrderStatus.PAID      -> OrderStatus.PAID
    com.coffeeshop.contracts.OrderStatus.PREPARING -> OrderStatus.PREPARING
    com.coffeeshop.contracts.OrderStatus.READY     -> OrderStatus.READY
    com.coffeeshop.contracts.OrderStatus.COMPLETED -> OrderStatus.COMPLETED
    com.coffeeshop.contracts.OrderStatus.CANCELLED -> OrderStatus.CANCELLED
}
