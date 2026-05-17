package com.coffeeshop.cart.internal.data.mapper

import com.coffeeshop.cart.api.domain.model.CartItem
import com.coffeeshop.cart.api.domain.model.CartItemModifier
import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.model.products.ModifierCategory
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Size
import com.coffeeshop.database.entity.CartItemEntity
import com.coffeeshop.database.entity.CartItemModifierEntity

internal fun CartItemEntity.toDomain(): CartItem = CartItem(
    uniqueCartItemID = ID(uniqueCartItemID),
    productId = ID(productId),
    productName = NameModel(productName),
    imageUrl = imageUrl,
    price = price,
    size = size,
    quantity = quantity,
    comment = comment,
    selectedModifiers = modifiers.map { it.toDomain() },
)

internal fun CartItemModifierEntity.toDomain(): CartItemModifier = CartItemModifier(
    id = ID(id),
    name = name,
    price = price,
    category = ModifierCategory.valueOf(category),
)

internal fun CartItem.toEntity(): CartItemEntity = CartItemEntity(
    uniqueCartItemID = uniqueCartItemID.value,
    productId = productId.value,
    productName = productName.value,
    imageUrl = imageUrl,
    price = price,
    size = size,
    quantity = quantity,
    comment = comment,
    modifiers = selectedModifiers.map { it.toEntity() },
)

internal fun CartItemModifier.toEntity(): CartItemModifierEntity = CartItemModifierEntity(
    id = id.value,
    name = name,
    price = price,
    category = category.name,
)
