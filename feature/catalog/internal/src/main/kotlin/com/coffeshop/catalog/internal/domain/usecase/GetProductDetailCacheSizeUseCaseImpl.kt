package com.coffeshop.catalog.internal.domain.usecase

import com.coffeshop.catalog.api.domain.repository.CatalogRepository
import com.coffeshop.catalog.api.domain.usecase.GetProductDetailCacheSizeUseCase
import javax.inject.Inject

internal class GetProductDetailCacheSizeUseCaseImpl
@Inject constructor(
    private val repository: CatalogRepository
) : GetProductDetailCacheSizeUseCase {

    override fun invoke() = repository.getProductDetailCacheSize()
}