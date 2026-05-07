package com.coffeeshop.product_detail.api.domain.usecase

import com.coffeeshop.common.model.order.OrderItem
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.result.Result

interface CalculateProductTotalPriceUseCase {

    suspend operator fun invoke(orderItem: OrderItem): Result<Price>
}