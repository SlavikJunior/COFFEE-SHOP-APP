package com.coffeeshop.profile.internal.domain.usecase

import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.result.Result
import com.coffeeshop.profile.api.domain.repository.ProfileRepository
import com.coffeeshop.profile.api.domain.usecase.ChangeNameUseCase
import javax.inject.Inject

internal class ChangeNameUseCaseImpl
@Inject constructor(
    private val repository: ProfileRepository
) : ChangeNameUseCase {

    override suspend fun invoke(newName: NameModel): Result<NameModel> =
        repository.changeName(newName)
}