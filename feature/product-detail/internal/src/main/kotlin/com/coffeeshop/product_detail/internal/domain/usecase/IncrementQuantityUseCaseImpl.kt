package com.coffeeshop.product_detail.internal.domain.usecase

import com.coffeeshop.common.result.Result
import com.coffeeshop.product_detail.api.domain.repository.ProductDetailRepository
import com.coffeeshop.product_detail.api.domain.usecase.IncrementQuantityUseCase
import javax.inject.Inject
import kotlin.Int

internal class IncrementQuantityUseCaseImpl
@Inject constructor(
    private val repository: ProductDetailRepository
) : IncrementQuantityUseCase {

    override suspend fun invoke(current: Int): Result<Int> = repository.incrementQuantity(current)
}