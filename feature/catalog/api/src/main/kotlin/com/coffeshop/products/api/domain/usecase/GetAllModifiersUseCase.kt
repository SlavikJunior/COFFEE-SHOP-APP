package com.coffeshop.products.api.domain.usecase

import com.coffeeshop.common.model.products.Modifier
import com.coffeeshop.common.result.Result

interface GetAllModifiersUseCase {

    suspend operator fun invoke(): Result<List<Modifier>>
}