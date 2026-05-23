package com.coffeeshop.auth.internal.domain.usecase

import com.coffeeshop.auth.api.domain.repository.AuthRepository
import com.coffeeshop.auth.api.domain.usecase.VerifyFirebaseTokenUseCase
import com.coffeeshop.common.result.Result
import javax.inject.Inject

class VerifyFirebaseTokenUseCaseImpl
@Inject constructor(
    private val authRepository: AuthRepository
) : VerifyFirebaseTokenUseCase {

    override suspend fun invoke(idToken: String): Result<Boolean> =
        authRepository.verifyFirebaseToken(idToken)
}
