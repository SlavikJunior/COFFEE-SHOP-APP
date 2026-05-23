package com.coffeeshop.auth.internal.di

import com.coffeeshop.auth.internal.data.service.AuthService
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
internal object FeatureAuthModule {

    @Provides
    @AuthScope
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @[Provides AuthScope]
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}