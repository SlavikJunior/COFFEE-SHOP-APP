package com.coffeeshop.cart.internal.domain.usecase

import com.coffeeshop.cart.api.domain.repository.CartRepository
import com.coffeeshop.cart.api.domain.usecase.SaveCartStateUseCase
import javax.inject.Inject

internal class SaveCartStateUseCaseImpl
@Inject constructor(
    private val repository: CartRepository,
) : SaveCartStateUseCase {

    override suspend fun invoke() = repository.persistCart()
}
