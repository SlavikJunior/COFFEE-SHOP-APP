package com.coffeeshop.auth.internal.domain.usecase

import com.coffeeshop.auth.api.domain.repository.AuthRepository
import com.coffeeshop.auth.api.domain.usecase.RegisterByFirebaseIdTokenAndNameUseCase
import com.coffeeshop.common.model.auth.AuthStatus
import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.result.Result
import javax.inject.Inject

class RegisterByFirebaseIdTokenAndNameUseCaseImpl
@Inject constructor(
    private val authRepository: AuthRepository
) : RegisterByFirebaseIdTokenAndNameUseCase {

    override suspend operator fun invoke(
        idToken: String,
        name: NameModel
    ): Result<AuthStatus> {
        return authRepository.registerWithFirebase(idToken, name.value)
    }
}
