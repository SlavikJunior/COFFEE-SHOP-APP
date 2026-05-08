package com.coffeeshop.product_detail.internal.domain.usecase

import com.coffeeshop.common.result.Result
import com.coffeeshop.product_detail.api.domain.repository.ProductDetailRepository
import com.coffeeshop.product_detail.api.domain.usecase.DecrementQuantityUseCase
import javax.inject.Inject

internal class DecrementQuantityUseCaseImpl
@Inject constructor(
    private val repository: ProductDetailRepository
) : DecrementQuantityUseCase {

    override suspend fun invoke(current: Int): Result<Int> = repository.decrementQuantity(current)
}