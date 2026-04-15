package com.coffeshop.products.api.domain.repository

import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.common.model.products.Modifier
import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.result.Result

interface ProductsRepository {

    suspend fun getFullMenu(): Result<List<Product>>

    suspend fun getMenuByCategoryType(categoryType: CategoryType): Result<List<Product>>

    suspend fun getProductDetailByProductId(productId: ID): Result<ProductWithModifiers>

    suspend fun getAllModifiers(): Result<List<Modifier>>
}