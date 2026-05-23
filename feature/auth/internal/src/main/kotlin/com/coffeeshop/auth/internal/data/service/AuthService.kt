package com.coffeeshop.auth.internal.data.service

import com.coffeeshop.contracts.FirebaseRegisterRequest
import com.coffeeshop.contracts.FirebaseVerifyRequest
import com.coffeeshop.contracts.TokenPair
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("api/auth/firebase/verify")
    suspend fun verifyFirebaseToken(@Body request: FirebaseVerifyRequest): TokenPair

    @POST("api/auth/firebase/register")
    suspend fun registerWithFirebase(@Body request: FirebaseRegisterRequest): TokenPair
}