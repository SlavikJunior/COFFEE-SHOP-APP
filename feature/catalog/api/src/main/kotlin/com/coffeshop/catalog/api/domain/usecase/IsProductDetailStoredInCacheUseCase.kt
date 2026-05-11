package com.coffeshop.catalog.api.domain.usecase

import com.coffeeshop.common.result.Result
import com.coffeeshop.common.model.support.ID

interface IsProductDetailStoredInCacheUseCase {

    suspend operator fun invoke(key: ID): Result<Boolean>
}