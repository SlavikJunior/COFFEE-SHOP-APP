package com.coffeeshop.cart.api.domain.usecase

import com.coffeeshop.common.model.support.ID

interface RemoveFromCartUseCase {

    suspend operator fun invoke(uniqueCartItemID: ID)
}