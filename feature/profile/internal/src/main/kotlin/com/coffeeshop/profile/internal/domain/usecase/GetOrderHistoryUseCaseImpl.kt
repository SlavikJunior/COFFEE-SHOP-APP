package com.coffeeshop.profile.internal.domain.usecase

import com.coffeeshop.common.model.order.Order
import com.coffeeshop.common.result.Result
import com.coffeeshop.profile.api.domain.repository.ProfileRepository
import com.coffeeshop.profile.api.domain.usecase.GetOrderHistoryUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOrderHistoryUseCaseImpl
@Inject constructor(
    private val repository: ProfileRepository
) : GetOrderHistoryUseCase {

    override suspend fun invoke(): Flow<Result<List<Order>>> = repository.getOrderHistory()
}