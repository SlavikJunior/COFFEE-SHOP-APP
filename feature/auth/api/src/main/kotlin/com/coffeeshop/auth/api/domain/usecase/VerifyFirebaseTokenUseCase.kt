package com.coffeeshop.auth.api.domain.usecase

import com.coffeeshop.common.result.Result

interface VerifyFirebaseTokenUseCase {

    suspend operator fun invoke(idToken: String): Result<Boolean>
}