package com.coffeeshop.orderhistory.internal.data.service

import com.coffeeshop.contracts.OrderSummaryDto
import com.coffeeshop.contracts.PagedResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface OrderHistoryService {

    @GET("api/orders")
    suspend fun getOrderHistory(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): PagedResponse<OrderSummaryDto>
}
