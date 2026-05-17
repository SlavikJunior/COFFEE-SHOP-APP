package com.coffeeshop.cart.internal.domain.usecase

import com.coffeeshop.cart.api.domain.repository.CartRepository
import com.coffeeshop.cart.api.domain.usecase.GetTotalPriceFromCartUseCase
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.result.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetTotalPriceFromCartUseCaseImpl
@Inject constructor(
    private val repository: CartRepository
) : GetTotalPriceFromCartUseCase {

    override suspend fun invoke(): Flow<Result<Price>> = repository.getTotalPrice()
}