package com.coffeeshop.cart.api.domain.model

import com.coffeeshop.common.model.products.ModifierCategory
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Price

data class CartItemModifier(
    val id: ID,
    val name: String,
    val price: Price,
    val category: ModifierCategory,
)
