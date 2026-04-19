package com.coffeeshop.profile.internal.domain.usecase

import com.coffeeshop.common.model.user.UserEmail
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.result.asErrorResult
import com.coffeeshop.network.storage.RequestStorage
import com.coffeeshop.profile.api.domain.repository.ProfileRepository
import com.coffeeshop.profile.api.domain.usecase.ChangeEmailUseCase
import javax.inject.Inject

class ChangeEmailUseCaseImpl
@Inject constructor(
    private val profileRepository: ProfileRepository,
    private val requestStorage: RequestStorage
): ChangeEmailUseCase {

    override suspend fun invoke(newEmail: UserEmail): Result<UserEmail> {
        try {
            requestStorage.makeRequest()
        } catch (cause: Throwable) {
            return cause.asErrorResult()
        }
        return profileRepository.changeEmail(newEmail = newEmail)
    }
}