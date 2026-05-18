package com.coffeeshop.product_detail.internal.data.repositpry

import com.coffeeshop.cache.api.Cache
import com.coffeeshop.common.model.order.OrderItem
import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.result.asSuccessResult
import com.coffeeshop.di.qualifiers.DispatcherIO
import com.coffeeshop.di.qualifiers.InMemoryCache
import com.coffeeshop.product_detail.api.domain.repository.ProductDetailRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class ProductDetailRepositoryImpl
@Inject constructor(
    @param:DispatcherIO private val dispatcher: CoroutineDispatcher,
    @param:InMemoryCache private val productDetailCache: Cache<ID, ProductWithModifiers>
) : ProductDetailRepository {

    override suspend fun calculateProductTotalPrice(orderItem: OrderItem): Result<Price> =
        withContext(dispatcher) {
            return@withContext orderItem.totalPrice.asSuccessResult()
        }

    override suspend fun decrementQuantity(current: Int): Result<Int> {
        return Result.Success(maxOf(current - 1, 1))
    }

    override suspend fun incrementQuantity(current: Int): Result<Int> {
        return Result.Success(current + 1)
    }

    override suspend fun getProductFromCache(id: ID): Result<ProductWithModifiers> {
        return productDetailCache.get(id)
    }

    override suspend fun removeProductFromCache(id: ID): Result<ProductWithModifiers?> {
        return productDetailCache.remove(id)
    }
}