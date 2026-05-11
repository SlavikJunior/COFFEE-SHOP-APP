package com.coffeshop.catalog.internal.di

import com.coffeshop.catalog.internal.data.service.CatalogService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
internal object FeatureCatalogModule {

    @Provides
    @FeatureCatalogScope
    fun provideProductsService(
        retrofit: Retrofit
    ): CatalogService = retrofit.create(CatalogService::class.java)
}