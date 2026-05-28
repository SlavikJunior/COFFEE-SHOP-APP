package com.coffeeshop.activeorders.api.domain.repository

import com.coffeeshop.activeorders.api.domain.model.ActiveOrder
import com.coffeeshop.common.result.Result

interface ActiveOrdersRepository {
    suspend fun getActiveOrders(): Result<List<ActiveOrder>>
    suspend fun fetchOrder(orderId: Long): Result<ActiveOrder>
}
