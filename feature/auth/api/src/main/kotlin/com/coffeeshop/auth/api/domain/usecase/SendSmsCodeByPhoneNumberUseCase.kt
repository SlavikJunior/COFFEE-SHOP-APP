package com.coffeeshop.auth.api.domain.usecase

import com.coffeeshop.common.result.Result
import com.coffeeshop.common.model.auth.AuthStatus
import com.coffeeshop.common.model.auth.PhoneNumberModel

interface SendSmsCodeByPhoneNumberUseCase {

    suspend operator fun invoke(phoneNumber: PhoneNumberModel): Result<AuthStatus>
}