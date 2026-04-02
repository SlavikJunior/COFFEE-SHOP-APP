package com.coffeeshop.auth.internal.domain.usecase

import com.coffeeshop.auth.api.domain.repository.AuthRepository
import com.coffeeshop.auth.api.domain.usecase.VerifySmsCodeByPhoneNumberUseCase
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.model.PhoneNumberModel
import com.coffeeshop.common.model.SmsCodeModel
import javax.inject.Inject

class VerifySmsCodeByPhoneNumberUseCaseImpl
@Inject constructor(
    private val authRepository: AuthRepository
) : VerifySmsCodeByPhoneNumberUseCase {

    override suspend fun invoke(
        phoneNumber: PhoneNumberModel,
        smsCode: SmsCodeModel
    ): Result<Boolean> {
        return authRepository.verify(
            phoneNumber = phoneNumber,
            smsCode = smsCode
        )
    }
}