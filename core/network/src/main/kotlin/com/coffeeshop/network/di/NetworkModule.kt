package com.coffeeshop.network.di

import com.coffeeshop.buildconfig.api.BuildConfigProvider
import com.coffeeshop.network.interceptors.AuthInterceptor
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        buildConfigProvider: BuildConfigProvider,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        val callTimeOut = buildConfigProvider.getCallTimeOut()
        val readTimeOut = buildConfigProvider.getReadTimeOut()
        val writeTimeout = buildConfigProvider.getWriteTimeOut()

        return OkHttpClient.Builder()
            .callTimeout(timeout = callTimeOut.component1(), unit = callTimeOut.component2())
            .readTimeout(timeout = readTimeOut.component1(), unit = readTimeOut.component2())
            .writeTimeout(timeout = writeTimeout.component1(), unit = writeTimeout.component2())
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (buildConfigProvider.isDebugBuild()) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.BASIC
            }
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        buildConfigProvider: BuildConfigProvider
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(
                if (buildConfigProvider.isDebugBuild())
                    buildConfigProvider.getCoffeeShopTestBaseUrl()
                else buildConfigProvider.getCoffeeShopBaseUrl()
            )
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
    }
}