package com.coffeeshop.auth.api.domain.repository

import com.coffeeshop.common.result.Result
import com.coffeeshop.common.model.AuthStatus
import com.coffeeshop.common.model.NameModel
import com.coffeeshop.common.model.PhoneNumberModel
import com.coffeeshop.common.model.SmsCodeModel

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