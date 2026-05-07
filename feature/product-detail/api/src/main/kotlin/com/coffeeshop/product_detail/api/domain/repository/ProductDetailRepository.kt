package com.coffeeshop.product_detail.api.domain.repository

import com.coffeeshop.common.model.order.OrderItem
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.result.Result

interface ProductDetailRepository {

    suspend fun calculateProductTotalPrice(orderItem: OrderItem): Result<Price>

    suspend fun decrementQuantity(current: Int): Result<Int>

    suspend fun incrementQuantity(current: Int): Result<Int>
}