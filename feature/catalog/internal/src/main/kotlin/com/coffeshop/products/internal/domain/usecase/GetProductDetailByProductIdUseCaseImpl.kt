package com.coffeshop.products.internal.domain.usecase

import com.coffeeshop.common.model.support.ID
import com.coffeshop.products.api.domain.repository.ProductsRepository
import com.coffeshop.products.api.domain.usecase.GetProductDetailByProductIdUseCase
import javax.inject.Inject

internal class GetProductDetailByProductIdUseCaseImpl
@Inject constructor(
    private val repository: ProductsRepository,
) : GetProductDetailByProductIdUseCase {

    override suspend fun invoke(productId: ID) = repository.getProductDetailByProductId(productId)
}
