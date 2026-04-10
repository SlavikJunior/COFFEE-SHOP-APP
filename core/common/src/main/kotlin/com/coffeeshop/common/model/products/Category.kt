package com.coffeeshop.common.model.products

import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Name

data class Category(
    val categoryId: ID,
    val categoryName: Name,
    val categoryType: CategoryType,
    val sortOrder: Int,
)
