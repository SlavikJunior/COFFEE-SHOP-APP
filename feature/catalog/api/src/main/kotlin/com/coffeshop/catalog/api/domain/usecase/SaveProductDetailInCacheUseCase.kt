package com.coffeshop.catalog.api.domain.usecase

import com.coffeeshop.common.result.Result
import com.coffeeshop.common.model.products.ProductWithModifiers

interface SaveProductDetailInCacheUseCase {

    suspend operator fun invoke(productDetail: ProductWithModifiers): Result<Boolean>
}