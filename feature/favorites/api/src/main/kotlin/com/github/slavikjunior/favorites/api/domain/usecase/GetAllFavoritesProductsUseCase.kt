package com.github.slavikjunior.favorites.api.domain.usecase

import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.result.Result
import kotlinx.coroutines.flow.Flow

interface GetAllFavoritesProductsUseCase {
    operator fun invoke(): Flow<Result<List<Product>>>
}