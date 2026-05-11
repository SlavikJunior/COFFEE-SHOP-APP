package com.coffeshop.catalog.internal.domain.usecase

import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.result.Result
import com.coffeshop.catalog.api.domain.repository.CatalogRepository
import com.coffeshop.catalog.api.domain.usecase.RemoveProductDetailFromCacheUseCase
import javax.inject.Inject

internal class RemoveProductDetailFromCacheUseCaseImpl
@Inject constructor(
    private val repository: CatalogRepository
) : RemoveProductDetailFromCacheUseCase {

    override suspend fun invoke(key: ID): Result<ProductWithModifiers?> = repository.removeProductDetailFromCache(key)
}