package com.coffeeshop.profile.api.domain.usecase

import com.coffeeshop.common.model.order.Order
import com.coffeeshop.common.result.Result

interface GetOrderHistoryUseCase {

    suspend operator fun invoke(): Result<List<Order>>
}