package com.coffeshop.catalog.internal.domain.usecase

import com.coffeeshop.common.model.support.ID
import com.coffeshop.catalog.api.domain.repository.CatalogRepository
import com.coffeshop.catalog.api.domain.usecase.GetProductDetailByProductIdUseCase
import javax.inject.Inject

internal class GetProductDetailByProductIdUseCaseImpl
@Inject constructor(
    private val repository: CatalogRepository,
) : GetProductDetailByProductIdUseCase {

    override suspend fun invoke(productId: ID) = repository.getProductDetailByProductId(productId)
}
