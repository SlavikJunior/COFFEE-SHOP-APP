package com.coffeeshop.activeorders.api.domain.usecase

import com.coffeeshop.activeorders.api.domain.model.ActiveOrder
import com.coffeeshop.common.result.Result

interface GetActiveOrdersUseCase {
    suspend operator fun invoke(): Result<List<ActiveOrder>>
}
