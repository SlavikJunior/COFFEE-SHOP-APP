package com.coffeeshop.network.interceptors

import com.coffeeshop.network.TokenRepository
import dagger.Lazy
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor
@Inject constructor(
    private val tokenRepository: Lazy<TokenRepository>,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenRepository.get().accessToken
        val req = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else chain.request()

        return chain.proceed(req)
    }
}