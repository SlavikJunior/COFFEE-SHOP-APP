package com.github.slavikjunior.favorites.internal.data.repository

import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.result.Result
import com.coffeeshop.database.dao.CachedProductDao
import com.coffeeshop.database.dao.FavoriteProductDao
import com.coffeeshop.database.entity.FavoriteProduct
import com.github.slavikjunior.favorites.api.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

internal class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteProductDao: FavoriteProductDao,
    private val cachedProductDao: CachedProductDao,
) : FavoritesRepository {

    override fun getFAllFavoriteProducts(): Flow<Result<List<Product>>> =
        combine(
            favoriteProductDao.getLiveWhereDeletedAtIsNull(),
            cachedProductDao.getLiveWhereDeletedAtIsNull()
        ) { favorites, cached ->
            val favoriteIds = favorites.map { it.productId }.toSet()
            val products = cached
                .filter { it.productId in favoriteIds }
                .map { it.toDomain() }
            Result.Success(products) as Result<List<Product>>
        }.catch { emit(Result.Error(it) as Result<List<Product>>) }

    override suspend fun toggleProduct(productId: ID): Result<Boolean> =
        try {
            val existing = favoriteProductDao.findActiveByProductId(productId.value)
            if (existing != null) {
                favoriteProductDao.softDeleteByProductId(productId.value)
                Result.Success(false)
            } else {
                favoriteProductDao.upsert(FavoriteProduct(productId = productId.value))
                Result.Success(true)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
}
