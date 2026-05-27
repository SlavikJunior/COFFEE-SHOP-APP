package com.coffeeshop.activeorders.internal.domain.usecase

import com.coffeeshop.activeorders.api.domain.model.ActiveOrder
import com.coffeeshop.activeorders.api.domain.repository.ActiveOrdersRepository
import com.coffeeshop.activeorders.api.domain.usecase.GetActiveOrdersUseCase
import com.coffeeshop.common.result.Result
import javax.inject.Inject

internal class GetActiveOrdersUseCaseImpl
@Inject constructor(
    private val repository: ActiveOrdersRepository,
) : GetActiveOrdersUseCase {

    override suspend operator fun invoke(): Result<List<ActiveOrder>> =
        repository.getActiveOrders()
}
