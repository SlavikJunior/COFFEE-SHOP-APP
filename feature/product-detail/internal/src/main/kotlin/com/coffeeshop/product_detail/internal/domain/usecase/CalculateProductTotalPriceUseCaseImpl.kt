package com.coffeeshop.product_detail.internal.domain.usecase

import com.coffeeshop.common.model.order.OrderItem
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.result.Result
import com.coffeeshop.product_detail.api.domain.repository.ProductDetailRepository
import com.coffeeshop.product_detail.api.domain.usecase.CalculateProductTotalPriceUseCase
import javax.inject.Inject

internal class CalculateProductTotalPriceUseCaseImpl
@Inject constructor(
    private val repository: ProductDetailRepository
) : CalculateProductTotalPriceUseCase {

    override suspend fun invoke(orderItem: OrderItem): Result<Price> = repository.calculateProductTotalPrice(orderItem)
}