package com.coffeeshop.common.model.products

import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.model.support.Size


data class ProductWithModifiers(
    val productId: ID,
    val productName: NameModel,
    val description: String?,
    val category: Category,
    val prices: Map<Size, Price>,
    val availableSizes: Set<Size>,
    val imageUrl: String?,
    val isAvailable: Boolean,
    val compatibleModifiers: List<Modifier>,
)

fun ProductWithModifiers.toProduct(): Product =
    Product(
        productId = productId,
        productName = productName,
        description = description,
        category = category,
        prices = prices,
        availableSizes = availableSizes,
        imageUrl = imageUrl,
        isAvailable = isAvailable
    )