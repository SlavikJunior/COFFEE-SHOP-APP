package com.coffeeshop.auth.internal.domain.usecase

import com.coffeeshop.auth.api.domain.usecase.IsUserLoggedInUseCase
import com.coffeeshop.network.TokenRepository
import javax.inject.Inject

internal class IsUserLoggedInUseCaseImpl @Inject constructor(
    private val tokenRepository: TokenRepository
) : IsUserLoggedInUseCase {

    override fun invoke(): Boolean {
        return  tokenRepository.accessToken != null
    }
}