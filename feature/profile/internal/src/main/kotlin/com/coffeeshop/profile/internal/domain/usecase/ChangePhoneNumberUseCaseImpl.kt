package com.coffeeshop.profile.internal.domain.usecase

import com.coffeeshop.common.model.auth.PhoneNumberModel
import com.coffeeshop.common.result.Result
import com.coffeeshop.profile.api.domain.repository.ProfileRepository
import com.coffeeshop.profile.api.domain.usecase.ChangePhoneNumberUseCase
import javax.inject.Inject

class ChangePhoneNumberUseCaseImpl
@Inject constructor(
    private val repository: ProfileRepository
) : ChangePhoneNumberUseCase {

    override suspend fun invoke(newPhoneNumber: PhoneNumberModel): Result<PhoneNumberModel> =
        repository.changePhoneNumber(newPhoneNumber)
}