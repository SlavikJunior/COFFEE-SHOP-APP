package com.coffeshop.catalog.internal.domain.usecase

import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.result.Result
import com.coffeshop.catalog.api.domain.repository.CatalogRepository
import com.coffeshop.catalog.api.domain.usecase.GetProductDetailFromCacheUseCase
import javax.inject.Inject

internal class GetProductDetailFromCacheUseCaseImpl
@Inject constructor(
    private val repository: CatalogRepository
) : GetProductDetailFromCacheUseCase {

    override suspend fun invoke(id: ID): Result<ProductWithModifiers> = repository.getProductDetailFromCache(id)
}