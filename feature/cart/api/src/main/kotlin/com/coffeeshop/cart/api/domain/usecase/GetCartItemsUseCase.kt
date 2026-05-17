package com.coffeeshop.cart.api.domain.usecase

import com.coffeeshop.cart.api.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface GetCartItemsUseCase {

    operator fun invoke(): Flow<List<CartItem>>
}
