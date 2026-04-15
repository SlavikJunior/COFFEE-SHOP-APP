package com.coffeeshop.profile.api.domain.usecase

import com.coffeeshop.common.model.auth.AuthStatus
import com.coffeeshop.common.result.Result

interface LogoutUseCase {

    suspend operator fun invoke(): Result<AuthStatus>
}