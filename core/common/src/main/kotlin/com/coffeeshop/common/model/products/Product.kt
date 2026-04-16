package com.coffeeshop.common.model.products

import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.model.support.Size

data class Product(
    val productId: ID,
    val productName: NameModel,
    val description: String?,
    val category: Category,
    val prices: Map<Size, Price>,
    val availableSizes: Set<Size>,
    val imageUrl: String?,
    val isAvailable: Boolean
) {
    val categoryType: CategoryType get() = category.categoryType
}
