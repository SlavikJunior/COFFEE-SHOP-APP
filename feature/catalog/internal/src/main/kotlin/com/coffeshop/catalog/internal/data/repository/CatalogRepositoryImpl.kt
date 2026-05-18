package com.coffeshop.catalog.internal.data.repository

import android.util.Log
import com.coffeeshop.buildconfig.api.BuildConfigProvider
import com.coffeeshop.cache.api.Cache
import com.coffeeshop.common.model.products.CategoryType
import com.coffeeshop.common.model.products.Modifier
import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.result.asErrorResult
import com.coffeeshop.common.result.asSuccessResult
import com.coffeeshop.common.result.isSuccess
import com.coffeeshop.di.qualifiers.DispatcherIO
import com.coffeeshop.di.qualifiers.InMemoryCache
import com.coffeeshop.utils.resolvePhotoUrls
import com.coffeshop.catalog.api.domain.repository.CatalogRepository
import com.coffeshop.catalog.internal.data.mapper.toDomain
import com.coffeshop.catalog.internal.data.service.CatalogService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class CatalogRepositoryImpl
@Inject constructor(
    private val service: CatalogService,
    @param:DispatcherIO private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    @param:InMemoryCache private val productDetailCache: Cache<ID, ProductWithModifiers>,
    private val buildConfigProvider: BuildConfigProvider
) : CatalogRepository {

    override suspend fun getFullMenu(): Result<List<Product>> = withContext(dispatcher) {
        try {
            val response = service.getFullMenu()
            val products = response.categories.values
                .flatten()
                .resolvePhotoUrls(
                    baseUrl = if (buildConfigProvider.isDebugBuild())
                        buildConfigProvider.getCoffeeShopTestBaseUrl()
                    else buildConfigProvider.getCoffeeShopBaseUrl()
                )
                .map { it.toDomain() }

            Log.i(TAG, "response on get full menu: $response")

            products.asSuccessResult()
        } catch (cause: Throwable) {
            Log.e(TAG, "getFullMenu error: $cause")
            cause.asErrorResult()
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
                val baseUrl = if (buildConfigProvider.isDebugBuild())
                    buildConfigProvider.getCoffeeShopTestBaseUrl()
                else buildConfigProvider.getCoffeeShopBaseUrl()
                val dto = service.getProductDetail(productId.value)
                dto.copy(photoUrl = baseUrl + dto.photoUrl).toDomain().asSuccessResult()
            } catch (cause: Throwable) {
                Log.e(TAG, "getProductDetail error: $cause")
                cause.asErrorResult()
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

    override suspend fun saveProductDetailInCache(
        productDetail: ProductWithModifiers,
    ) = productDetailCache.put(productDetail.productId, productDetail)

    override suspend fun removeProductDetailFromCache(key: ID) = productDetailCache.remove(key)

    override suspend fun getProductDetailFromCache(key: ID): Result<ProductWithModifiers> {
        return try {
            productDetailCache.get(key)
        } catch (cause: Throwable) {
            Result.Error(cause)
        }
    }

    override suspend fun isProductDetailStoredInCache(key: ID): Result<Boolean> =
        try {
            productDetailCache.isStoredByKey(key)
        } catch (cause: Throwable) {
            cause.asErrorResult()
        }

    override fun getProductDetailCacheSize() = productDetailCache.size()

    private companion object {
        const val TAG = "CatalogRepository"
    }
}
