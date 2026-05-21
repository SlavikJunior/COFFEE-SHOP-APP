package com.coffeeshop.network

import com.coffeeshop.common.result.Result
import com.coffeeshop.common.result.asErrorResult
import com.coffeeshop.common.result.asSuccessResult
import com.coffeeshop.contracts.RefreshTokenRequest
import com.coffeeshop.di.qualifiers.DispatcherIO
import com.coffeeshop.network.storage.TokenStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class TokenRepositoryImpl
@Inject constructor(
    private val service: TokenService,
    private val tokenStorage: TokenStorage,
    @param:DispatcherIO private val dispatcher: CoroutineDispatcher
) : TokenRepository {

    private val _sessionExpired = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val sessionExpired: SharedFlow<Unit> = _sessionExpired.asSharedFlow()

    override var accessToken: String?
        get() = tokenStorage.accessToken
        set(value) { tokenStorage.accessToken = value }
    override var refreshToken: String?
        get() = tokenStorage.refreshToken
        set(value) { tokenStorage.refreshToken = value }
    override var userId: String?
        get() = tokenStorage.userId
        set(value) { tokenStorage.userId = value }

    override suspend fun updateToken(): Result<String> {
        val refreshToken = tokenStorage.refreshToken
            ?: run {
                tokenStorage.clear()
                _sessionExpired.emit(Unit)
                return Result.Error(Throwable("No refresh token stored"))
            }

        return withContext(dispatcher) {
            try {
                val tokenPair = service.refresh(RefreshTokenRequest(refreshToken = refreshToken))
                tokenStorage.update(
                    accessToken = tokenPair.accessToken,
                    refreshToken = tokenPair.refreshToken,
                    userId = tokenPair.userId
                )
                tokenPair.accessToken.asSuccessResult()
            } catch (cause: Throwable) {
                tokenStorage.clear()
                _sessionExpired.emit(Unit)
                cause.asErrorResult()
            }
        }
    }
}
