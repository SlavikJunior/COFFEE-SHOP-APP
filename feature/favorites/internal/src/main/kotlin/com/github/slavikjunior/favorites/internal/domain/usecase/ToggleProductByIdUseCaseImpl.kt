package com.github.slavikjunior.favorites.internal.domain.usecase

import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.result.Result
import com.github.slavikjunior.favorites.api.domain.repository.FavoritesRepository
import com.github.slavikjunior.favorites.api.domain.usecase.ToggleProductByIdUseCase
import com.github.slavikjunior.favorites.internal.di.FeatureFavoritesScope
import javax.inject.Inject

@FeatureFavoritesScope
internal class ToggleProductByIdUseCaseImpl @Inject constructor(
    private val repository: FavoritesRepository
) : ToggleProductByIdUseCase {
    override suspend fun invoke(productId: ID): Result<Boolean> = repository.toggleProduct(productId)
}
