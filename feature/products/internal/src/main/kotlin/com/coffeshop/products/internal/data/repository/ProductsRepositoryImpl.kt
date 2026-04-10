package com.coffeshop.products.internal.data.repository

import android.util.Log
import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.common.model.products.Modifier
import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.result.Result
import com.coffeshop.products.api.domain.repository.ProductsRepository
import com.coffeshop.products.internal.data.mapper.toDomain
import com.coffeshop.products.internal.data.service.ProductsService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class ProductsRepositoryImpl
@Inject constructor(
    private val service: ProductsService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ProductsRepository {

    override suspend fun getFullMenu(): Result<List<Product>> = withContext(dispatcher) {
        try {
            val response = service.getFullMenu()
            val products = response.categories.values
                .flatten()
                .map { it.toDomain() }
            Result.Success(products)
        } catch (cause: Throwable) {
            Log.e(TAG, "getFullMenu error: $cause")
            Result.Error(cause)
        }
    }

    override suspend fun getMenuByCategoryType(categoryType: CategoryType): Result<List<Product>> =
        withContext(dispatcher) {
            try {
                val response = service.getFullMenu()
                val products = response.categories
                    .filterKeys { key -> key == categoryType.name }
                    .values
                    .flatten()
                    .map { it.toDomain() }
                Result.Success(products)
            } catch (cause: Throwable) {
                Log.e(TAG, "getMenuByCategoryType error: $cause")
                Result.Error(cause)
            }
        }

    override suspend fun getProductDetailByProductId(productId: ID): Result<ProductWithModifiers> =
        withContext(dispatcher) {
            try {
                val dto = service.getProductDetail(productId.value.toLong())
                Result.Success(dto.toDomain())
            } catch (cause: Throwable) {
                Log.e(TAG, "getProductDetail error: $cause")
                Result.Error(cause)
            }
        }

    override suspend fun getAllModifiers(): Result<List<Modifier>> = withContext(dispatcher) {
        try {
            val response = service.getFullMenu()
            val firstItem = response.categories.values.flatten().firstOrNull()
                ?: return@withContext Result.Success(emptyList())
            val detail = service.getProductDetail(firstItem.id)
            Result.Success(detail.compatibleModifiers.map { it.toDomain() })
        } catch (cause: Throwable) {
            Log.e(TAG, "getAllModifiers error: $cause")
            Result.Error(cause)
        }
    }

    private companion object {
        const val TAG = "ProductsRepository"
    }
}
