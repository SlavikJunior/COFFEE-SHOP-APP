package com.coffeeshop.cart.internal.domain.usecase

import com.coffeeshop.cart.api.domain.repository.CartRepository
import com.coffeeshop.cart.api.domain.usecase.RemoveFromCartUseCase
import com.coffeeshop.common.model.support.ID
import javax.inject.Inject

internal class RemoveFromCartUseCaseImpl
@Inject constructor(
    private val repository: CartRepository
) : RemoveFromCartUseCase {

    override suspend fun invoke(uniqueCartItemID: ID) = repository.removeFromCart(uniqueCartItemID)
}