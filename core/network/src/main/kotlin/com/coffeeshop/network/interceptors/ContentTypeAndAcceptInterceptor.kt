package com.coffeeshop.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ContentTypeAndAcceptInterceptor
@Inject constructor(
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request().newBuilder()
            .removeHeader("Content-Type")
            .removeHeader("Accept")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()

        return chain.proceed(req)
    }
}