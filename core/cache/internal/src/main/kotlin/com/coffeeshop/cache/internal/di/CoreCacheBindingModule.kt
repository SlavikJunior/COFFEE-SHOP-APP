package com.coffeeshop.cache.internal.di

import com.coffeeshop.cache.api.Cache
import com.coffeeshop.cache.internal.impl.ProductDetailInMemoryCacheImpl
import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.di.qualifiers.InMemoryCache
import dagger.Binds
import dagger.Module

@Module
internal interface CoreCacheBindingModule {

    @[Binds CoreCacheScope InMemoryCache]
    fun bindProductDetailCache(impl: ProductDetailInMemoryCacheImpl): Cache<ID, ProductWithModifiers>
}