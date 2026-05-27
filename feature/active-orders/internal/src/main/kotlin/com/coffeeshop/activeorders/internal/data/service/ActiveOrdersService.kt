package com.coffeeshop.activeorders.internal.data.service

import com.coffeeshop.contracts.OrderDetailDto
import com.coffeeshop.contracts.OrderSummaryDto
import retrofit2.http.GET
import retrofit2.http.Path

internal interface ActiveOrdersService {

    @GET("api/orders/active")
    suspend fun getActiveOrders(): List<OrderSummaryDto>

    @GET("api/orders/{id}")
    suspend fun getOrderDetail(@Path("id") id: Long): OrderDetailDto
}
