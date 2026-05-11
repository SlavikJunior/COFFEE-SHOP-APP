package com.coffeshop.catalog.internal.domain.usecase

import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.result.Result
import com.coffeshop.catalog.api.domain.repository.CatalogRepository
import com.coffeshop.catalog.api.domain.usecase.IsProductDetailStoredInCacheUseCase
import javax.inject.Inject

internal class IsProductDetailStoredInCacheUseCaseImpl
@Inject constructor(
    private val repository: CatalogRepository
) : IsProductDetailStoredInCacheUseCase {

    override suspend fun invoke(key: ID): Result<Boolean> = repository.isProductDetailStoredInCache(key)
}