package com.github.slavikjunior.favorites.api.domain.usecase

import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.result.Result

interface ToggleProductByIdUseCase {

    suspend operator fun invoke(productId: ID): Result<Boolean>
}