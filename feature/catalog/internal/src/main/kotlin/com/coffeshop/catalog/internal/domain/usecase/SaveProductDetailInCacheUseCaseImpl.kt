package com.coffeshop.catalog.internal.domain.usecase

import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.result.Result
import com.coffeshop.catalog.api.domain.repository.CatalogRepository
import com.coffeshop.catalog.api.domain.usecase.SaveProductDetailInCacheUseCase
import javax.inject.Inject

internal class SaveProductDetailInCacheUseCaseImpl
@Inject constructor(
    private val repository: CatalogRepository
) : SaveProductDetailInCacheUseCase {

    override suspend fun invoke(productDetail: ProductWithModifiers): Result<Boolean> = repository.saveProductDetailInCache(productDetail)
}