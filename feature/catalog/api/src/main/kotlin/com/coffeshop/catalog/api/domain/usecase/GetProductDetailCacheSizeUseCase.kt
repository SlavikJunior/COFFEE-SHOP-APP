package com.coffeshop.catalog.api.domain.usecase

import com.coffeeshop.common.result.Result

interface GetProductDetailCacheSizeUseCase {

    operator fun invoke(): Result<Int>
}