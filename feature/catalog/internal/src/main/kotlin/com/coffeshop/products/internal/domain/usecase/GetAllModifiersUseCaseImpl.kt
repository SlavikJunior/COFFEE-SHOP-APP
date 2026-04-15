package com.coffeshop.products.internal.domain.usecase

import com.coffeshop.products.api.domain.repository.ProductsRepository
import com.coffeshop.products.api.domain.usecase.GetAllModifiersUseCase
import javax.inject.Inject

internal class GetAllModifiersUseCaseImpl
@Inject constructor(
    private val repository: ProductsRepository,
) : GetAllModifiersUseCase {

    override suspend fun invoke() = repository.getAllModifiers()
}
