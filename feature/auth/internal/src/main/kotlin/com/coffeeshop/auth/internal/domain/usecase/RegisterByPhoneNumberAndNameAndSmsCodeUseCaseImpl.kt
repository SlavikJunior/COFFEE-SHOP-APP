package com.coffeeshop.auth.internal.domain.usecase

import com.coffeeshop.auth.api.domain.repository.AuthRepository
import com.coffeeshop.auth.api.domain.usecase.RegisterByPhoneNumberAndNameAndSmsCodeUseCase
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.model.auth.AuthStatus
import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.model.auth.PhoneNumberModel
import com.coffeeshop.common.model.auth.SmsCodeModel
import javax.inject.Inject

class RegisterByPhoneNumberAndNameAndSmsCodeUseCaseImpl
@Inject constructor(
    private val authRepository: AuthRepository
) : RegisterByPhoneNumberAndNameAndSmsCodeUseCase {

    override suspend operator fun invoke(
        phoneNumber: PhoneNumberModel,
        name: NameModel,
        smsCode: SmsCodeModel
    ): Result<AuthStatus> {
        return authRepository.register(
            name = name,
            phoneNumber = phoneNumber,
            smsCode = smsCode
        )
    }
}

