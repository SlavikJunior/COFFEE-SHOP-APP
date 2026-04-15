package com.coffeshop.products.internal.domain.usecase

import com.coffeeshop.common.model.products.CategoryType
import com.coffeshop.products.api.domain.repository.ProductsRepository
import com.coffeshop.products.api.domain.usecase.GetMenuByCategoryTypeUseCase
import javax.inject.Inject

internal class GetMenuByCategoryTypeUseCaseImpl
@Inject constructor(
    private val repository: ProductsRepository,
) : GetMenuByCategoryTypeUseCase {

    override suspend fun invoke(categoryType: CategoryType) =
        repository.getMenuByCategoryType(categoryType)
}
