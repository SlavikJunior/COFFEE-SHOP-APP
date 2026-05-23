package com.coffeeshop.cache.internal.impl

import android.util.Log
import com.coffeeshop.cache.api.Cache
import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.result.asErrorResult
import com.coffeeshop.common.result.asSuccessResult
import com.coffeeshop.logger.api.CoffeeshopLogger
import com.coffeeshop.logger.api.tagOf
import com.coffeeshop.utils.getOrThrow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

internal class ProductDetailInMemoryCacheImpl
@Inject constructor(
    private val productDetailCacheMap: MutableMap<ID, ProductWithModifiers>,
    private val logger: CoffeeshopLogger,
) : Cache<ID, ProductWithModifiers> {

//    val mutex = Mutex()

    override suspend fun put(key: ID, value: ProductWithModifiers): Result<Boolean> =
//            mutex.withLock {
        try {
                logger.debug(TAG.tagOf(), "putting in cacheMap by key: $key value: $value")

                productDetailCacheMap[key] = value
                true.asSuccessResult()
        } catch (cause: Throwable) {
            cause.asErrorResult()
        }
//            }

    override suspend fun get(key: ID): Result<ProductWithModifiers> =
        try {
            logger.debug(TAG.tagOf(), "getting from cacheMap by key: $key")

            productDetailCacheMap.getOrThrow(key).asSuccessResult()
        } catch (cause: Throwable) {
            cause.asErrorResult()
        }

    override suspend fun remove(key: ID): Result<ProductWithModifiers?> =
        try {
            logger.debug(TAG.tagOf(), "removing from cacheMap by key: $key")

            val item: ProductWithModifiers? = productDetailCacheMap.remove(key)
            item.asSuccessResult()
        } catch (cause: Throwable) {
            cause.asErrorResult()
        }

    override fun size(): Result<Int> = productDetailCacheMap.size.asSuccessResult()

    override fun isStoredByValue(value: ProductWithModifiers) = productDetailCacheMap.containsValue(value).asSuccessResult()

    override fun isStoredByKey(key: ID): Result<Boolean> = productDetailCacheMap.containsKey(key).asSuccessResult()

    private companion object {
        const val TAG = "ProductDetailInMemoryCacheImpl"
    }
}