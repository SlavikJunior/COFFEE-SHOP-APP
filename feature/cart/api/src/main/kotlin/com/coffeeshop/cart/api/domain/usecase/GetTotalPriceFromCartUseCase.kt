package com.coffeeshop.cart.api.domain.usecase

import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.result.Result
import kotlinx.coroutines.flow.Flow

interface GetTotalPriceFromCartUseCase {

    suspend operator fun invoke(): Flow<Result<Price>>
}