package com.coffeeshop.network.interceptors

import com.coffeeshop.common.result.Result
import com.coffeeshop.di.qualifiers.DispatcherIO
import com.coffeeshop.network.TokenRepository
import com.coffeeshop.network.storage.TokenStorage
import dagger.Lazy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator
@Inject constructor(
    private val tokenRepository: Lazy<TokenRepository>,
    private val tokenStorage: TokenStorage,
    @param:DispatcherIO private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Authenticator {

    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header(RETRY_HEADER) != null) return null

        val newToken = runBlocking {
            try {
                mutex.withLock {
                    withContext(dispatcher) {
                        val currentToken = tokenStorage.accessToken
                        val requestToken =
                            response.request.header("Authorization")?.removePrefix("Bearer ")
                        if (currentToken != null && currentToken != requestToken) {
                            return@withContext currentToken
                        }
                        when (val result = tokenRepository.get().updateToken()) {
                            is Result.Success -> result.data
                            else -> null
                        }
                    }
                }
            } catch (_: Throwable) {
                return@runBlocking null
            }
        } ?: return null

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newToken")
            .header(RETRY_HEADER, "true")
            .build()
    }

    private companion object {
        const val RETRY_HEADER = "Authorization-Retry"
    }
}
