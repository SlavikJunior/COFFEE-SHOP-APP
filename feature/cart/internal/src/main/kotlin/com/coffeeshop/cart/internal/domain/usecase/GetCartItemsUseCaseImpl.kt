package com.coffeeshop.cart.internal.domain.usecase

import com.coffeeshop.cart.api.domain.model.CartItem
import com.coffeeshop.cart.api.domain.repository.CartRepository
import com.coffeeshop.cart.api.domain.usecase.GetCartItemsUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetCartItemsUseCaseImpl
@Inject constructor(
    private val repository: CartRepository,
) : GetCartItemsUseCase {

    override fun invoke(): Flow<List<CartItem>> = repository.getItems()
}
