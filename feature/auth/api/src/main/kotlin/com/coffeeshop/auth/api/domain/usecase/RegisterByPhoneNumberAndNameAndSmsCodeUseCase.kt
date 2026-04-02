package com.coffeeshop.auth.api.domain.usecase

import com.coffeeshop.common.model.AuthStatus
import com.coffeeshop.common.model.NameModel
import com.coffeeshop.common.model.PhoneNumberModel
import com.coffeeshop.common.model.SmsCodeModel
import com.coffeeshop.common.result.Result

interface RegisterByPhoneNumberAndNameAndSmsCodeUseCase {

    suspend operator fun invoke(
        phoneNumber: PhoneNumberModel,
        name: NameModel,
        smsCode: SmsCodeModel
    ): Result<AuthStatus>
}