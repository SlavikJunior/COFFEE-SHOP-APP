package com.coffeeshop.profile.internal.domain.usecase

import com.coffeeshop.common.model.auth.AuthStatus
import com.coffeeshop.common.result.Result
import com.coffeeshop.profile.api.domain.repository.ProfileRepository
import com.coffeeshop.profile.api.domain.usecase.LogoutUseCase
import javax.inject.Inject

class LogoutUseCaseImpl
@Inject constructor(
    private val repository: ProfileRepository
) : LogoutUseCase {

    override suspend fun invoke(): Result<AuthStatus> = repository.logout()
}