package com.coffeeshop.orderhistory.internal.data.mapper

import com.coffeeshop.contracts.OrderStatus
import com.coffeeshop.contracts.OrderSummaryDto
import com.coffeeshop.orderhistory.api.domain.model.OrderSummary
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
private val outputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale.forLanguageTag("ru"))

internal fun OrderSummaryDto.toOrderSummary(): OrderSummary = OrderSummary(
    id = id,
    status = orderStatus.toRussianLabel(),
    createdAt = runCatching {
        LocalDateTime.parse(createdAt, inputFormatter).format(outputFormatter)
    }.getOrDefault(createdAt),
    totalAmount = totalPrice,
)

private fun OrderStatus.toRussianLabel(): String = when (this) {
    OrderStatus.PENDING -> "Ожидает"
    OrderStatus.PAID -> "Оплачен"
    OrderStatus.PREPARING -> "Готовится"
    OrderStatus.READY -> "Готов"
    OrderStatus.COMPLETED -> "Выполнен"
    OrderStatus.CANCELLED -> "Отменён"
}
