package com.coffeeshop.profile.internal.domain.usecase

import com.coffeeshop.common.model.user.User
import com.coffeeshop.common.result.Result
import com.coffeeshop.profile.api.domain.repository.ProfileRepository
import com.coffeeshop.profile.api.domain.usecase.GetProfileUseCase
import javax.inject.Inject

internal class GetProfileUseCaseImpl
@Inject constructor(
    private val repository: ProfileRepository
) : GetProfileUseCase {

    override suspend fun invoke(): Result<User> = repository.getProfile()
}