package com.coffeeshop.profile.api.domain.usecase

import com.coffeeshop.common.model.order.Order
import com.coffeeshop.common.result.Result
import kotlinx.coroutines.flow.Flow

interface GetOrderHistoryUseCase {

    suspend operator fun invoke(): Flow<Result<List<Order>>>
}