package com.coffeeshop.orderhistory.api.domain.model

data class OrderSummary(
    val id: Long,
    val status: String,
    val createdAt: String,
    val totalAmount: String,
)