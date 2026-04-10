package com.coffeshop.products.internal.domain.usecase

import com.coffeshop.products.api.domain.repository.ProductsRepository
import com.coffeshop.products.api.domain.usecase.GetFullMenuUseCase
import javax.inject.Inject

internal class GetFullMenuUseCaseImpl
@Inject constructor(
    private val repository: ProductsRepository,
) : GetFullMenuUseCase {

    override suspend fun invoke() = repository.getFullMenu()
}
