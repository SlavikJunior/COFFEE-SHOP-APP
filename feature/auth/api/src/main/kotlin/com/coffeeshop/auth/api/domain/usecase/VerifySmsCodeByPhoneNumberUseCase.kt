package com.coffeeshop.auth.api.domain.usecase

import com.coffeeshop.common.result.Result
import com.coffeeshop.common.model.auth.PhoneNumberModel
import com.coffeeshop.common.model.auth.SmsCodeModel

interface VerifySmsCodeByPhoneNumberUseCase {

    suspend operator fun invoke(
        phoneNumber: PhoneNumberModel,
        smsCode: SmsCodeModel
    ) : Result<Boolean>
}