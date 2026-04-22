package com.coffeshop.catalog.internal.domain.usecase

import com.coffeshop.catalog.api.domain.repository.CatalogRepository
import com.coffeshop.catalog.api.domain.usecase.GetFullMenuUseCase
import javax.inject.Inject

internal class GetFullMenuUseCaseImpl
@Inject constructor(
    private val repository: CatalogRepository,
) : GetFullMenuUseCase {

    override suspend fun invoke() = repository.getFullMenu()
}
