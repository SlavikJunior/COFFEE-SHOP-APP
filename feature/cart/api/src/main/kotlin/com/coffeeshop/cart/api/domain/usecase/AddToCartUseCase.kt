package com.coffeeshop.cart.api.domain.usecase

import com.coffeeshop.cart.api.domain.model.CartItem

interface AddToCartUseCase {

    suspend operator fun invoke(item: CartItem)
}