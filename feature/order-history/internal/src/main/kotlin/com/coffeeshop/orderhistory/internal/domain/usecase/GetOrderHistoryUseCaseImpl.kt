package com.coffeeshop.orderhistory.internal.domain.usecase

import com.coffeeshop.common.result.Result
import com.coffeeshop.orderhistory.api.domain.model.OrderSummary
import com.coffeeshop.orderhistory.api.domain.repository.OrderHistoryRepository
import com.coffeeshop.orderhistory.api.domain.usecase.GetOrderHistoryUseCase
import javax.inject.Inject

internal class GetOrderHistoryUseCaseImpl
@Inject constructor(
    private val repository: OrderHistoryRepository,
) : GetOrderHistoryUseCase {

    override suspend fun invoke(): Result<List<OrderSummary>> = repository.getOrderHistory()
}
