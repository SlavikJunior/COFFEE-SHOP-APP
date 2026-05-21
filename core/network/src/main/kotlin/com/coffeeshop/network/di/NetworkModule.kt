package com.coffeeshop.network.di

import com.coffeeshop.buildconfig.api.BuildConfigProvider
import com.coffeeshop.network.TokenService
import com.coffeeshop.network.interceptors.AuthInterceptor
import com.coffeeshop.network.interceptors.ContentTypeAndAcceptInterceptor
import com.coffeeshop.network.interceptors.TokenAuthenticator
import com.coffeeshop.network.TokenRepository
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
internal object NetworkModule {

    @Provides
    @NetworkScope
    fun provideOkHttpClient(
        buildConfigProvider: BuildConfigProvider,
        authInterceptor: AuthInterceptor,
        contentTypeAndAcceptInterceptor: ContentTypeAndAcceptInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        val callTimeOut = buildConfigProvider.getCallTimeOut()
        val readTimeOut = buildConfigProvider.getReadTimeOut()
        val writeTimeout = buildConfigProvider.getWriteTimeOut()

        return OkHttpClient.Builder()
            .callTimeout(timeout = callTimeOut.component1(), unit = callTimeOut.component2())
            .readTimeout(timeout = readTimeOut.component1(), unit = readTimeOut.component2())
            .writeTimeout(timeout = writeTimeout.component1(), unit = writeTimeout.component2())
            .addInterceptor(authInterceptor)
            .addInterceptor(contentTypeAndAcceptInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (buildConfigProvider.isDebugBuild()) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
            })
            .build()
    }

    @Provides
    @NetworkScope
    fun provideRetrofit(
        json: Json,
        client: OkHttpClient,
        buildConfigProvider: BuildConfigProvider
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(
                if (buildConfigProvider.isDebugBuild())
                    buildConfigProvider.getCoffeeShopTestBaseUrl()
                else buildConfigProvider.getCoffeeShopBaseUrl()
            )
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
    }

    @Provides
    @NetworkScope
    fun provideTokenService(retrofit: Retrofit): TokenService =
        retrofit.create(TokenService::class.java)

    @Provides
    @NetworkScope
    fun provideSessionExpiredFlow(tokenRepository: TokenRepository): SharedFlow<Unit> =
        tokenRepository.sessionExpired
}
