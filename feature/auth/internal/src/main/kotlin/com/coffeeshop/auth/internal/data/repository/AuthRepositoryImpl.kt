package com.coffeeshop.auth.internal.data.repository

import android.util.Log
import com.coffeeshop.auth.api.domain.repository.AuthRepository
import com.coffeeshop.auth.internal.data.service.AuthService
import com.coffeeshop.auth.internal.di.AuthScope
import com.coffeeshop.common.model.auth.AuthStatus
import com.coffeeshop.common.result.Result
import com.coffeeshop.contracts.FirebaseRegisterRequest
import com.coffeeshop.contracts.FirebaseVerifyRequest
import com.coffeeshop.di.qualifiers.DispatcherIO
import com.coffeeshop.network.NotificationsRepository
import com.coffeeshop.network.TokenRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

@AuthScope
internal class AuthRepositoryImpl
@Inject constructor(
    private val service: AuthService,
    private val tokenRepository: TokenRepository,
    private val notificationsRepository: NotificationsRepository,
    @param:DispatcherIO private val dispatcher: CoroutineDispatcher,
) : AuthRepository {

    override suspend fun verifyFirebaseToken(idToken: String): Result<Boolean> =
        withContext(dispatcher) {
            try {
                val tokenPair = service.verifyFirebaseToken(FirebaseVerifyRequest(idToken))
                tokenRepository.accessToken = tokenPair.accessToken
                tokenRepository.refreshToken = tokenPair.refreshToken
                tokenRepository.userId = tokenPair.userId.toString()
                registerFcmToken()
                Result.Success(true)
            } catch (e: HttpException) {
                if (e.code() == 404) Result.Success(false)
                else Result.Error(e)
            } catch (cause: Throwable) {
                Log.e(TAG, "verifyFirebaseToken error: $cause")
                Result.Error(cause)
            }
        }

    override suspend fun registerWithFirebase(idToken: String, name: String): Result<AuthStatus> =
        withContext(dispatcher) {
            try {
                val tokenPair = service.registerWithFirebase(FirebaseRegisterRequest(idToken, name))
                tokenRepository.accessToken = tokenPair.accessToken
                tokenRepository.refreshToken = tokenPair.refreshToken
                tokenRepository.userId = tokenPair.userId.toString()
                registerFcmToken()
                Result.Success(AuthStatus.User)
            } catch (cause: Throwable) {
                Log.e(TAG, "registerWithFirebase error: $cause")
                Result.Error(cause)
            }
        }

    override suspend fun sendNewToken(token: String) {}

    override suspend fun deleteToken(token: String) {}

    private suspend fun registerFcmToken() {
        try {
            val fcmToken = FirebaseMessaging.getInstance().token.await()
            notificationsRepository.registerToken(fcmToken)
        } catch (cause: Throwable) {
            Log.w(TAG, "registerFcmToken failed", cause)
        }
    }

    private companion object {
        const val TAG = "AuthRepository"
    }
}
