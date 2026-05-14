package com.coffeeshop.profile.api.domain.usecase

import com.coffeeshop.common.model.user.UserNotificationsEnabled
import com.coffeeshop.common.result.Result

interface ToggleGetNotificationsUseCase {

    suspend operator fun invoke(): Result<UserNotificationsEnabled>
}