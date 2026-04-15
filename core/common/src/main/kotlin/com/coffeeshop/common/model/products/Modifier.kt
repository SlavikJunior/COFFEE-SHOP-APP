package com.coffeeshop.common.model.products

import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Name
import com.coffeeshop.common.model.support.Price

data class Modifier(
    val additiveId: ID,
    val additiveName: Name,
    val price: Price,
    val category: ModifierCategory,
    val isAvailable: Boolean,
)
