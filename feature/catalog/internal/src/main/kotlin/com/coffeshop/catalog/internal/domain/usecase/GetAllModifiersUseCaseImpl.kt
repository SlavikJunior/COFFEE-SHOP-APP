package com.coffeshop.catalog.internal.domain.usecase

import com.coffeshop.catalog.api.domain.repository.CatalogRepository
import com.coffeshop.catalog.api.domain.usecase.GetAllModifiersUseCase
import javax.inject.Inject

internal class GetAllModifiersUseCaseImpl
@Inject constructor(
    private val repository: CatalogRepository,
) : GetAllModifiersUseCase {

    override suspend fun invoke() = repository.getAllModifiers()
}
