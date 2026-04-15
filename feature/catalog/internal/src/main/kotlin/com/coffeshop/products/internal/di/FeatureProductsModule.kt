package com.coffeshop.products.internal.di

import com.coffeeshop.buildconfig.api.BuildConfigProvider
import com.coffeshop.products.internal.data.service.ProductsService
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module(includes = [FeatureProductsBindingModule::class])
internal abstract class FeatureProductsModule {

    companion object {

        @Provides
        @Singleton
        fun provideProductsService(
            client: OkHttpClient,
            buildConfigProvider: BuildConfigProvider
        ): ProductsService {
            return Retrofit.Builder()
                .baseUrl(buildConfigProvider.getCoffeeShopBaseUrl())
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .client(client)
                .build()
                .create(ProductsService::class.java)
        }
    }
}
