package com.coffeeshop.auth.internal.di

import android.content.Context
import com.coffeeshop.auth.internal.data.service.AuthService
import com.coffeeshop.buildconfig.api.BuildConfigProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [FeatureAuthModule::class]
)
interface FeatureAuthComponent {

    val authService: AuthService

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance buildConfigProvider: BuildConfigProvider
        ): FeatureAuthComponent
    }
}