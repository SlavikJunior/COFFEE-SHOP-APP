package com.coffeshop.catalog.api.domain.usecase

import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.result.Result

interface GetFullMenuUseCase {

    suspend operator fun invoke(): Result<List<Product>>
}