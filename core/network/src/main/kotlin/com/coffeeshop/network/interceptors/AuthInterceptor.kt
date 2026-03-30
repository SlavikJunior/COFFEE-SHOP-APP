package com.coffeeshop.network.interceptors

import com.coffeeshop.network.storage.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor
@Inject constructor(
    private val tokenStorage: TokenStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenStorage.accessToken
        val req = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else chain.request()

        return chain.proceed(req)
    }
}