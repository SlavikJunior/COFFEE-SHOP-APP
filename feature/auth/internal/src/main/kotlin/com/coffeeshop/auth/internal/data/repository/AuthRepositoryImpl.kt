package com.coffeeshop.auth.internal.data.repository

import android.util.Log
import com.coffeeshop.auth.api.domain.repository.AuthRepository
import com.coffeeshop.auth.internal.data.service.AuthService
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.model.AuthStatus
import com.coffeeshop.common.model.NameModel
import com.coffeeshop.common.model.PhoneNumberModel
import com.coffeeshop.common.model.SmsCodeModel
import com.coffeeshop.contracts.RegisterRequest
import com.coffeeshop.contracts.SendSmsRequest
import com.coffeeshop.contracts.TokenPair
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

    override suspend fun sendSms(phoneNumber: PhoneNumberModel): Result<AuthStatus> {
        withContext(dispatcher) {
            try {
                service.sendSms(
                    request = SendSmsRequest(
                        phone = phoneNumber.value
                    )
                )
            } catch (cause: Throwable) {
                Log.e(TAG, "Error while send sms, cause: $cause")
                return@withContext Result.Error(cause)
            }
        }
        return Result.Success(AuthStatus.WaitSms)
    }

    override suspend fun register(
        name: NameModel,
        phoneNumber: PhoneNumberModel,
        smsCode: SmsCodeModel
    ): Result<AuthStatus> {
        var tokenPair: TokenPair? = null
        withContext(dispatcher) {
            try {
                tokenPair = service.register(
                    request = RegisterRequest(
                        name = name.value,
                        phone = phoneNumber.value,
                        code = smsCode.value
                    )
                )
            } catch (cause: Throwable) {
                Log.e(TAG, "Error while register, cause: $cause")
                return@withContext Result.Error(cause)
            }
        }

        if (tokenPair != null) {
            TODO("tokenStorage.accessToken = tokenPair...")
            return Result.Success(AuthStatus.User)
        } else TODO("handle nulls")
    }

    override suspend fun verify(
        phoneNumber: PhoneNumberModel,
        smsCode: SmsCodeModel
    ) : Result<Boolean> {
        var tokenPair: TokenPair? = null
        withContext(dispatcher) {
            try {
                tokenPair = service.verify(
                    request = VerifyOtpRequest(
                        phone = phoneNumber.value,
                        code = smsCode.value
                    )
                )
            } catch (cause: Throwable) {
                Log.e(TAG, "Error while register, cause: $cause")
                return@withContext Result.Error(cause)
            }
        }

        if (tokenPair != null) {
            TODO("tokenStorage.accessToken = tokenPair...")
            return Result.Success(true)
        } else TODO("handle nulls")
    }

    override suspend fun loginByPhoneNumber(phoneNumber: PhoneNumberModel): Result<AuthStatus> {
        TODO("Not yet implemented")
    }

    override suspend fun logoutByPhoneNumber(phoneNumber: PhoneNumberModel): Result<AuthStatus> {
        TODO("Not yet implemented")
    }

    private companion object {
        const val TAG = "TEST TAG"
    }
}