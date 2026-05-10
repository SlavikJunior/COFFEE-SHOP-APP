package com.coffeeshop.cache.internal.di

import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import dagger.Module
import dagger.Provides

@Module
object CoreCacheModule {

    @[Provides CoreCacheScope]
    fun provideProductDetailCacheMap(): MutableMap<ID, ProductWithModifiers> = mutableMapOf()
}