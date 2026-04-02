package com.coffeeshop.auth.internal.data.service

import com.coffeeshop.contracts.RegisterRequest
import com.coffeeshop.contracts.SendSmsRequest
import com.coffeeshop.contracts.TokenPair
import com.coffeeshop.contracts.VerifyOtpRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): TokenPair

    @POST("send-sms")
    fun sendSms(@Body request: SendSmsRequest)

    @POST("verify")
    fun verify(
        @Body request: VerifyOtpRequest
    ): TokenPair

}