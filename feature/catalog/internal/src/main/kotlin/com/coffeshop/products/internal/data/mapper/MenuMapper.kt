package com.coffeshop.products.internal.data.mapper

import com.coffeeshop.common.model.products.Category
import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.common.model.products.Modifier
import com.coffeeshop.common.model.products.ModifierCategory
import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Name
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.model.support.Size
import com.coffeeshop.contracts.MenuCategory
import com.coffeeshop.contracts.MenuItemDetailDto
import com.coffeeshop.contracts.MenuItemSummaryDto
import com.coffeeshop.contracts.ModifierDto
import com.coffeeshop.contracts.VolumeDto

internal fun MenuItemSummaryDto.toDomain(): Product {
    val sizeMap = volumes.toSizeMap()
    return Product(
        productId = ID(id.toString()),
        productName = Name(name),
        description = description,
        category = category.toDomain(),
        prices = sizeMap,
        availableSizes = sizeMap.keys,
        imageUrl = photoUrl,
        isAvailable = isAvailable,
    )
}

internal fun MenuItemDetailDto.toDomain(): ProductWithModifiers {
    val sizeMap = volumes.toSizeMap()
    return ProductWithModifiers(
        productId = ID(id.toString()),
        productName = Name(name),
        description = description,
        category = category.toDomain(),
        prices = sizeMap,
        availableSizes = sizeMap.keys,
        imageUrl = photoUrl,
        isAvailable = isAvailable,
        compatibleModifiers = compatibleModifiers.map { it.toDomain() },
    )
}

internal fun ModifierDto.toDomain(): Modifier = Modifier(
    additiveId = ID(id.toString()),
    additiveName = Name(name),
    price = price.toPrice(),
    category = ModifierCategory.valueOf(category.name),
    isAvailable = true,
)

private fun MenuCategory.toDomain(): Category = Category(
    categoryId = ID(name),
    categoryName = Name(name),
    categoryType = CategoryType.valueOf(name),
    sortOrder = ordinal,
)

private fun List<VolumeDto>.toSizeMap(): Map<Size, Price> =
    mapNotNull { volume ->
        val size = Size.entries.find { it.ml == volume.volumeMl } ?: return@mapNotNull null
        size to volume.price.toPrice()
    }.toMap()

private fun String.toPrice(): Price {
    return try {
        val parts = split(".")
        val first = parts[0].toIntOrNull() ?: 0
        val second = parts.getOrNull(1)?.take(2)?.padEnd(2, '0')?.toIntOrNull() ?: 0
        Price(firstPart = first, secondPart = second)
    } catch (_: Exception) {
        Price(firstPart = 0, secondPart = 0)
    }
}
