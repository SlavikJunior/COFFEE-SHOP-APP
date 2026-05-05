package com.coffeeshop.profile.api.domain.usecase

import com.coffeeshop.common.model.user.User
import com.coffeeshop.common.result.Result

interface GetProfileUseCase {

    suspend operator fun invoke(): Result<User>
}