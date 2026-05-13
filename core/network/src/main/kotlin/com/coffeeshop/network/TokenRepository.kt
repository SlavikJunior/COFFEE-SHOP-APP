package com.coffeeshop.network

import com.coffeeshop.common.result.Result
import com.coffeeshop.common.result.asErrorResult
import com.coffeeshop.common.result.asSuccessResult
import com.coffeeshop.contracts.RefreshTokenRequest
import com.coffeeshop.di.qualifiers.DispatcherIO
import com.coffeeshop.network.storage.TokenStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TokenRepository
@Inject constructor(
    private val service: TokenService,
    private val tokenStorage: TokenStorage,
    @param:DispatcherIO private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun updateToken(): Result<String> {
        val refreshToken = tokenStorage.refreshToken
            ?: return Result.Error(Throwable("No refresh token stored"))

        return withContext(dispatcher) {
            try {
                val tokenPair = service.refresh(RefreshTokenRequest(refreshToken = refreshToken))
                tokenStorage.update(
                    accessToken = tokenPair.accessToken,
                    refreshToken = tokenPair.refreshToken
                )
                tokenPair.accessToken.asSuccessResult()
            } catch (cause: Throwable) {
                cause.asErrorResult()
            }
        }
    }
}
