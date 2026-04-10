package com.coffeshop.products.api.domain.usecase

import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.result.Result

interface GetMenuByCategoryTypeUseCase {

    suspend operator fun invoke(categoryType: CategoryType): Result<List<Product>>
}