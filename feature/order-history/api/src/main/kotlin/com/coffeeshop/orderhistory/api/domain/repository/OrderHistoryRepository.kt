package com.coffeeshop.orderhistory.api.domain.repository

import com.coffeeshop.common.result.Result
import com.coffeeshop.orderhistory.api.domain.model.OrderSummary

interface OrderHistoryRepository {

    suspend fun getOrderHistory(page: Int = 0, size: Int = 20): Result<List<OrderSummary>>
}
