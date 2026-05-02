package com.coffeeshop.coffeeshopapp

import android.content.Context
import com.coffeeshop.di.qualifiers.ApplicationContext
import com.coffeshop.deps.AppDeps
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@CoffeeShopAppScope
@Component(
    dependencies = [AppDeps::class]
)
interface CoffeeShopAppComponent {

    @ApplicationContext
    val applicationContext: Context

    @Component.Builder
    interface Builder {

        @[BindsInstance ApplicationContext]
        fun applicationContext(applicationContext: Context): Builder

        fun appDeps(appDeps: AppDeps): Builder

        fun build(): CoffeeShopAppComponent
    }

    fun inject(activity: MainActivity)
}