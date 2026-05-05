package com.coffeeshop.profile.internal.domain.usecase

import com.coffeeshop.common.model.user.UserNotificationsEnabled
import com.coffeeshop.common.result.Result
import com.coffeeshop.profile.api.domain.repository.ProfileRepository
import com.coffeeshop.profile.api.domain.usecase.ToggleGetNotificationsUseCase
import javax.inject.Inject

class ToggleGetNotificationsUseCaseImpl
@Inject constructor(
    private val repository: ProfileRepository
) : ToggleGetNotificationsUseCase {

    override suspend fun invoke(): Result<UserNotificationsEnabled> = repository.toggleGetNotifications()
}