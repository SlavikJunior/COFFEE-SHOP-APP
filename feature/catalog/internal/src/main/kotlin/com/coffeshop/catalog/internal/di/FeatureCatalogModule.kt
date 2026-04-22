package com.coffeshop.catalog.internal.di

import com.coffeeshop.buildconfig.api.BuildConfigProvider
import com.coffeshop.catalog.internal.data.service.CatalogService
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
internal object FeatureCatalogModule {

    @Provides
    @CatalogScope
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @CatalogScope
    fun provideProductsService(
        client: OkHttpClient,
        buildConfigProvider: BuildConfigProvider
    ): CatalogService {
        return Retrofit.Builder()
            .baseUrl(buildConfigProvider.getCoffeeShopBaseUrl())
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
            .create(CatalogService::class.java)
    }
}