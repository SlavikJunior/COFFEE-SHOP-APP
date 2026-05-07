package com.coffeeshop.product_detail.api.domain.usecase

import com.coffeeshop.common.result.Result

interface IncrementQuantityUseCase {

    suspend operator fun invoke(current: Int): Result<Int>
}