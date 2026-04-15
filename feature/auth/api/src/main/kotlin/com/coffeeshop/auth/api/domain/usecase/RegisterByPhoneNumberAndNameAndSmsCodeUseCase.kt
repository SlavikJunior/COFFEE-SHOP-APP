package com.coffeeshop.auth.api.domain.usecase

import com.coffeeshop.common.model.auth.AuthStatus
import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.model.auth.PhoneNumberModel
import com.coffeeshop.common.model.auth.SmsCodeModel
import com.coffeeshop.common.result.Result

interface RegisterByPhoneNumberAndNameAndSmsCodeUseCase {

    suspend operator fun invoke(
        phoneNumber: PhoneNumberModel,
        name: NameModel,
        smsCode: SmsCodeModel
    ): Result<AuthStatus>
}