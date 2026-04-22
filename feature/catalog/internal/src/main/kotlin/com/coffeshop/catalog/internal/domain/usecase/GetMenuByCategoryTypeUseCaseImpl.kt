package com.coffeshop.catalog.internal.domain.usecase

import com.coffeeshop.common.model.products.CategoryType
import com.coffeshop.catalog.api.domain.repository.CatalogRepository
import com.coffeshop.catalog.api.domain.usecase.GetMenuByCategoryTypeUseCase
import javax.inject.Inject

internal class GetMenuByCategoryTypeUseCaseImpl
@Inject constructor(
    private val repository: CatalogRepository,
) : GetMenuByCategoryTypeUseCase {

    override suspend fun invoke(categoryType: CategoryType) =
        repository.getMenuByCategoryType(categoryType)
}
