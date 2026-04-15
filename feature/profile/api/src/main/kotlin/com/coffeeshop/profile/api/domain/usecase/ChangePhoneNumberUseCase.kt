package com.coffeeshop.profile.api.domain.usecase

import com.coffeeshop.common.model.auth.PhoneNumberModel
import com.coffeeshop.common.result.Result

interface ChangePhoneNumberUseCase {

    suspend operator fun invoke(newPhoneNumber: PhoneNumberModel): Result<PhoneNumberModel>
}