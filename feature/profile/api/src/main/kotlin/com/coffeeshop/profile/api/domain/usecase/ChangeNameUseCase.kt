package com.coffeeshop.profile.api.domain.usecase

import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.result.Result

interface ChangeNameUseCase {

    suspend operator fun invoke(newName: NameModel): Result<NameModel>
}