package com.coffeeshop.profile.api.domain.usecase

import com.coffeeshop.common.model.user.UserEmail
import com.coffeeshop.common.result.Result

interface ChangeEmailUseCase {

    suspend operator fun invoke(newEmail: UserEmail): Result<UserEmail>
}