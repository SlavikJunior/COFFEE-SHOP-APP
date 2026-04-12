package com.coffeeshop.coffeeshopapp

import android.content.Context
import com.coffeeshop.auth.internal.di.FeatureAuthDeps
import com.coffeeshop.di.qualifiers.ApplicationContext
import com.coffeshop.deps.AppDeps
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppNavigationModule::class],
    dependencies = [
        AppDeps::class,
        FeatureAuthDeps::class
    ]
)
internal interface CoffeeShopAppComponent {

    @ApplicationContext
    val applicationContext: Context

    @Component.Builder
    interface Builder {

        @BindsInstance
        @ApplicationContext
        fun applicationContext(applicationContext: Context): Builder

        @BindsInstance
        fun appDeps(appDeps: AppDeps): Builder

        @BindsInstance
        fun featureAuthDeps(featureAuthDeps: FeatureAuthDeps): Builder

         fun build(): CoffeeShopAppComponent
    }

    fun inject(activity: MainActivity)
}