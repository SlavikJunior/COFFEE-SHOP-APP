package com.github.slavikjunior.favorites.api.domain.repository

import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.model.support.ID
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    fun getFAllFavoriteProducts(): Flow<com.coffeeshop.common.result.Result<List<Product>>>

    suspend fun toggleProduct(productId: ID): com.coffeeshop.common.result.Result<Boolean>
}