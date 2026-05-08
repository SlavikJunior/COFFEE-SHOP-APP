package com.coffeeshop.product_detail.api.domain.repository

import com.coffeeshop.common.model.order.OrderItem
import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.result.Result

interface ProductDetailRepository {

    suspend fun calculateProductTotalPrice(orderItem: OrderItem): Result<Price>

    suspend fun decrementQuantity(current: Int): Result<Int>

    suspend fun incrementQuantity(current: Int): Result<Int>

    suspend fun getProductFromCache(id: ID): Result<ProductWithModifiers>

    suspend fun removeProductFromCache(id: ID): Result<ProductWithModifiers?>
}