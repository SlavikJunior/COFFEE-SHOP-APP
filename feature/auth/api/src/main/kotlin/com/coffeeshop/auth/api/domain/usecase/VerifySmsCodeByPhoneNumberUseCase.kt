package com.coffeeshop.auth.api.domain.usecase

import com.coffeeshop.common.result.Result
import com.coffeeshop.common.model.PhoneNumberModel
import com.coffeeshop.common.model.SmsCodeModel

interface VerifySmsCodeByPhoneNumberUseCase {

    suspend operator fun invoke(
        phoneNumber: PhoneNumberModel,
        smsCode: SmsCodeModel
    ) : Result<Boolean>
}