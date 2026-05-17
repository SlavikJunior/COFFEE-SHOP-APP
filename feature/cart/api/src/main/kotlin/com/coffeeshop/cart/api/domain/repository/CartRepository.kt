package com.coffeeshop.cart.api.domain.repository

import com.coffeeshop.cart.api.domain.model.CartItem
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.result.Result
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    suspend fun initData()

    suspend fun addToCart(item: CartItem)

    suspend fun removeFromCart(uniqueCartItemID: ID)

    fun getItems(): Flow<List<CartItem>>

    fun getTotalPrice(): Flow<Result<Price>>

    suspend fun persistCart()
}