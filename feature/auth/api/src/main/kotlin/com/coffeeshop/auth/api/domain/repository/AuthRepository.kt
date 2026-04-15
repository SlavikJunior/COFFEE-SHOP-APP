package com.coffeeshop.auth.api.domain.repository

import com.coffeeshop.common.result.Result
import com.coffeeshop.common.model.auth.AuthStatus
import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.model.auth.PhoneNumberModel
import com.coffeeshop.common.model.auth.SmsCodeModel

interface AuthRepository {

    suspend fun sendSms(phoneNumber: PhoneNumberModel): Result<AuthStatus>

    suspend fun register(
        name: NameModel,
        phoneNumber: PhoneNumberModel,
        smsCode: SmsCodeModel
    ): Result<AuthStatus>

    suspend fun verify(
        phoneNumber: PhoneNumberModel,
        smsCode: SmsCodeModel
    ): Result<Boolean>

    suspend fun loginByPhoneNumber(phoneNumber: PhoneNumberModel): Result<AuthStatus>

    suspend fun logoutByPhoneNumber(phoneNumber: PhoneNumberModel): Result<AuthStatus>
}