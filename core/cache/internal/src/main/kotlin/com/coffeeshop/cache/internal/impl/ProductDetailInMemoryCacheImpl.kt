package com.coffeeshop.cache.internal.impl

import com.coffeeshop.cache.api.Cache
import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.result.asErrorResult
import com.coffeeshop.common.result.asSuccessResult
import com.coffeeshop.utils.getOrThrow
import javax.inject.Inject

internal class ProductDetailInMemoryCacheImpl
@Inject constructor(
    private val productDetailCacheMap: MutableMap<ID, ProductWithModifiers>
) : Cache<ID, ProductWithModifiers> {

    override suspend fun put(key: ID, value: ProductWithModifiers): Result<Boolean> =
        try {
            productDetailCacheMap[key] = value
            true.asSuccessResult()
        } catch (cause: Throwable) {
            cause.asErrorResult()
        }

    override suspend fun get(key: ID): Result<ProductWithModifiers> =
        try {
            productDetailCacheMap.getOrThrow(key).asSuccessResult()
        } catch (cause: Throwable) {
            cause.asErrorResult()
        }

    override suspend fun remove(key: ID): Result<ProductWithModifiers?> =
        try {
            val item: ProductWithModifiers? = productDetailCacheMap.remove(key)
            item.asSuccessResult()
        } catch (cause: Throwable) {
            cause.asErrorResult()
        }

    override fun size(): Result<Int> = productDetailCacheMap.size.asSuccessResult()

    override fun isStoredByValue(value: ProductWithModifiers) = productDetailCacheMap.containsValue(value).asSuccessResult()

    override fun isStoredByKey(key: ID): Result<Boolean> = productDetailCacheMap.containsKey(key).asSuccessResult()
}