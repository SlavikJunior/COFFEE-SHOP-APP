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

@Module(includes = [FeatureAuthBindingModule::class])
internal object FeatureAuthModule {

        @Provides
        @AuthScope
        fun provideAuthService(
            retrofit: Retrofit,
        ): AuthService {
            return retrofit.create(AuthService::class.java)
        }
}