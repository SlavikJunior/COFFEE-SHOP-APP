package com.coffeeshop.auth.internal.di

import com.coffeeshop.auth.internal.data.service.AuthService
import com.coffeeshop.buildconfig.api.BuildConfigProvider
import com.coffeeshop.network.interceptors.AuthInterceptor
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module(includes = [
    FeatureAuthNavigationModule::class,
    FeatureAuthBindingModule::class
])
internal abstract class FeatureAuthModule {

    companion object {

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
                .build()
        }

        @Provides
        @Singleton
        fun provideAuthService(
            client: OkHttpClient,
            buildConfigProvider: BuildConfigProvider
        ): AuthService {
            return Retrofit.Builder()
                .baseUrl(buildConfigProvider.getCoffeeShopBaseUrl())
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .client(client)
                .build().create(AuthService::class.java)
        }
    }
}