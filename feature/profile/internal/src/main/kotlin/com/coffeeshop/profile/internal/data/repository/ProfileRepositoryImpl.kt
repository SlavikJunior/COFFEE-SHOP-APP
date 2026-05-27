package com.coffeeshop.profile.internal.data.repository

import android.util.Log
import com.coffeeshop.common.model.auth.AuthStatus
import com.coffeeshop.common.model.user.User
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.result.asErrorResult
import com.coffeeshop.common.result.asSuccessResult
import com.coffeeshop.di.qualifiers.DispatcherIO
import com.coffeeshop.network.TokenRepository
import com.coffeeshop.profile.api.domain.repository.ProfileRepository
import com.coffeeshop.profile.internal.data.toUser
import com.coffeeshop.profile.internal.data.service.ProfileService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class ProfileRepositoryImpl
@Inject constructor(
    private val profileService: ProfileService,
    private val tokenRepository: TokenRepository,
    @param:DispatcherIO private val dispatcher: CoroutineDispatcher,
) : ProfileRepository {

    override suspend fun getProfile(): Result<User> = withContext(dispatcher) {
        try {
            profileService.getProfile().toUser().asSuccessResult()
        } catch (cause: Throwable) {
            Log.e(TAG, "getProfile failed: $cause")
            cause.asErrorResult()
        }
    }

    override suspend fun logout(): Result<AuthStatus> = withContext(dispatcher) {
        try {
            tokenRepository.accessToken = null
            tokenRepository.refreshToken = null
            tokenRepository.userId = null
            AuthStatus.Guest.asSuccessResult()
        } catch (cause: Throwable) {
            Log.e(TAG, "logout failed: $cause")
            cause.asErrorResult()
        }
    }

    private companion object {
        const val TAG = "ProfileRepository"
    }
}
