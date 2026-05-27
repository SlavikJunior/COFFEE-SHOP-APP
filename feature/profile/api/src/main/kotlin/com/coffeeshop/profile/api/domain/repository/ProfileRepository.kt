package com.coffeeshop.profile.api.domain.repository

import com.coffeeshop.common.model.auth.AuthStatus
import com.coffeeshop.common.model.user.User
import com.coffeeshop.common.result.Result

interface ProfileRepository {

    suspend fun getProfile(): Result<User>

    suspend fun logout(): Result<AuthStatus>
}
