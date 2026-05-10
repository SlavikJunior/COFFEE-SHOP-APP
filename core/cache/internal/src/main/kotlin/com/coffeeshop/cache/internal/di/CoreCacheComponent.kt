package com.coffeeshop.cache.internal.di

import com.coffeeshop.cache.api.Cache
import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.di.qualifiers.InMemoryCache
import dagger.Component

@Component(
    modules = [
        CoreCacheModule::class,
        CoreCacheBindingModule::class
    ]
)
@CoreCacheScope
interface CoreCacheComponent {

    @InMemoryCache fun productDetailCache(): Cache<ID, ProductWithModifiers>
}