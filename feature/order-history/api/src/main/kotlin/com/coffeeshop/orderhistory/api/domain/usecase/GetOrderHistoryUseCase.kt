package com.coffeeshop.orderhistory.api.domain.usecase

import com.coffeeshop.common.result.Result
import com.coffeeshop.orderhistory.api.domain.model.OrderSummary

interface GetOrderHistoryUseCase {

    suspend operator fun invoke(): Result<List<OrderSummary>>
}
