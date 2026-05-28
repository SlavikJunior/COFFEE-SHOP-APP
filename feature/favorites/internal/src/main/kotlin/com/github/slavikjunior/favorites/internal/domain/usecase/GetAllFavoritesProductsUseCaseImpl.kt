package com.github.slavikjunior.favorites.internal.domain.usecase

import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.result.Result
import com.github.slavikjunior.favorites.api.domain.repository.FavoritesRepository
import com.github.slavikjunior.favorites.api.domain.usecase.GetAllFavoritesProductsUseCase
import com.github.slavikjunior.favorites.internal.di.FeatureFavoritesScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@FeatureFavoritesScope
internal class GetAllFavoritesProductsUseCaseImpl @Inject constructor(
    private val repository: FavoritesRepository
) : GetAllFavoritesProductsUseCase {
    override fun invoke(): Flow<Result<List<Product>>> = repository.getFAllFavoriteProducts()
}
