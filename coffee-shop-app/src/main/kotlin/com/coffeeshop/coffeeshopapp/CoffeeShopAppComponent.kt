package com.coffeeshop.coffeeshopapp

import android.content.Context
import com.coffeeshop.di.qualifiers.ApplicationContext
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppNavigationModule::class])
interface CoffeeShopAppComponent {

    @ApplicationContext
    val applicationContext: Context

    @Component.Builder
    interface Builder {

        @BindsInstance
        @ApplicationContext
        fun applicationContext(applicationContext: Context): Builder

        fun build(): CoffeeShopAppComponent
    }

    fun inject(activity: MainActivity)
}