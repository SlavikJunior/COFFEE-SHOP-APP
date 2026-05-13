package com.coffeeshop.network

import com.coffeeshop.contracts.RefreshTokenRequest
import com.coffeeshop.contracts.TokenPair
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenService {

    @POST("api/auth/refresh")
    suspend fun refresh(@Body refreshTokenRequest: RefreshTokenRequest): TokenPair
}