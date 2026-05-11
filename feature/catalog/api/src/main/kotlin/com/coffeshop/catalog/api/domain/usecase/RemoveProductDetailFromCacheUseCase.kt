package com.coffeshop.catalog.api.domain.usecase

import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.result.Result

interface RemoveProductDetailFromCacheUseCase {

    suspend operator fun invoke(key: ID): Result<ProductWithModifiers?>
}