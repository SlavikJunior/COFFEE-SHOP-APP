package com.coffeeshop.common.model.products

import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.common.model.support.ID
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val categoryId: ID,
    val categoryName: NameModel,
    val categoryType: CategoryType,
    val sortOrder: Int,
)
