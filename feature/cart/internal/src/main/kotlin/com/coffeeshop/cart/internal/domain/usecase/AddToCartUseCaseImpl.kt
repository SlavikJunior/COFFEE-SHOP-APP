package com.coffeeshop.cart.internal.domain.usecase

import com.coffeeshop.cart.api.domain.model.CartItem
import com.coffeeshop.cart.api.domain.repository.CartRepository
import com.coffeeshop.cart.api.domain.usecase.AddToCartUseCase
import javax.inject.Inject

internal class AddToCartUseCaseImpl
@Inject constructor(
    private val repository: CartRepository
) : AddToCartUseCase {

    override suspend fun invoke(item: CartItem) = repository.addToCart(item)
}