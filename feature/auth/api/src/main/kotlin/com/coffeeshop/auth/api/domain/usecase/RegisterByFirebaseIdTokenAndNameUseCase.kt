package com.coffeeshop.auth.api.domain.usecase

import com.coffeeshop.common.model.auth.AuthStatus
import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.result.Result

interface RegisterByFirebaseIdTokenAndNameUseCase {

    suspend operator fun invoke(
        idToken: String,
        name: NameModel
    ): Result<AuthStatus>
}