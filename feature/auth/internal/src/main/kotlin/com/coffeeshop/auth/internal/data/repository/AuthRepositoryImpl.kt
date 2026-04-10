package com.coffeeshop.auth.internal.data.repository

import android.util.Log
import com.coffeeshop.auth.api.domain.repository.AuthRepository
import com.coffeeshop.auth.internal.data.service.AuthService
import com.coffeeshop.common.model.auth.AuthStatus
import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.model.auth.PhoneNumberModel
import com.coffeeshop.common.model.auth.SmsCodeModel
import com.coffeeshop.common.result.Result
import com.coffeeshop.contracts.RegisterRequest
import com.coffeeshop.contracts.SendSmsRequest
import com.coffeeshop.contracts.VerifyOtpRequest
import com.coffeeshop.network.storage.TokenStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl
@Inject constructor(
    private val service: AuthService,
    private val tokenStorage: TokenStorage,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {

    override suspend fun sendSms(phoneNumber: PhoneNumberModel): Result<AuthStatus> =
        withContext(dispatcher) {
            try {
                service.sendSms(SendSmsRequest(phone = phoneNumber.value))
                Result.Success(AuthStatus.WaitSms)
            } catch (cause: Throwable) {
                Log.e(TAG, "sendSms error: $cause")
                Result.Error(cause)
            }
        }

    override suspend fun register(
        name: NameModel,
        phoneNumber: PhoneNumberModel,
        smsCode: SmsCodeModel
    ): Result<AuthStatus> = withContext(dispatcher) {
        try {
            val tokenPair = service.register(
                RegisterRequest(
                    name = name.value,
                    phone = phoneNumber.value,
                    code = smsCode.value
                )
            )
            tokenStorage.accessToken = tokenPair.accessToken
            tokenStorage.refreshToken = tokenPair.refreshToken
            Result.Success(AuthStatus.User)
        } catch (cause: Throwable) {
            Log.e(TAG, "register error: $cause")
            Result.Error(cause)
        }
    }

    override suspend fun verify(
        phoneNumber: PhoneNumberModel,
        smsCode: SmsCodeModel
    ): Result<Boolean> = withContext(dispatcher) {
        try {
            val tokenPair = service.verify(
                VerifyOtpRequest(
                    phone = phoneNumber.value,
                    code = smsCode.value
                )
            )
            tokenStorage.accessToken = tokenPair.accessToken
            tokenStorage.refreshToken = tokenPair.refreshToken
            Result.Success(true)
        } catch (cause: Throwable) {
            Log.e(TAG, "verify error: $cause")
            Result.Error(cause)
        }
    }

    override suspend fun loginByPhoneNumber(phoneNumber: PhoneNumberModel): Result<AuthStatus> =
        sendSms(phoneNumber)

    override suspend fun logoutByPhoneNumber(phoneNumber: PhoneNumberModel): Result<AuthStatus> {
        tokenStorage.clear()
        return Result.Success(AuthStatus.Guest)
    }

    private companion object {
        const val TAG = "AuthRepository"
    }
}
