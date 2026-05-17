package com.coffeeshop.cart.api.domain.model

import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.model.support.Size

data class CartItem(
    val uniqueCartItemID: ID = ID.random(),
    val productId: ID,
    val productName: NameModel,
    val imageUrl: String?,
    val price: Price,
    val size: Size,
    val quantity: Int,
    val comment: String,
    val selectedModifiers: List<CartItemModifier> = emptyList(),
)