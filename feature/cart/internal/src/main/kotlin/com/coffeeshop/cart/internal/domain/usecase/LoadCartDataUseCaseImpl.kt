package com.coffeeshop.cart.internal.domain.usecase

import com.coffeeshop.cart.api.domain.repository.CartRepository
import com.coffeeshop.cart.api.domain.usecase.LoadCartDataUseCase
import javax.inject.Inject

internal class LoadCartDataUseCaseImpl
@Inject constructor(
    private val repository: CartRepository,
) : LoadCartDataUseCase {

    override suspend fun invoke() = repository.initData()
}
