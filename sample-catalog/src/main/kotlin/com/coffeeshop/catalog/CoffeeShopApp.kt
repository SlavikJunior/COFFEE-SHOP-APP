package com.coffeeshop.catalog

import android.app.Application
import android.content.Context
import com.coffeshop.deps.AppDeps
import com.coffeshop.products.internal.di.DaggerFeatureCatalogComponent
import com.coffeshop.products.internal.di.FeatureCatalogComponent
import com.coffeeshop.network.di.DaggerNetworkComponent

class CoffeeShopApp : Application() {

    internal lateinit var coffeeShopAppComponent: CoffeeShopAppComponent
    internal lateinit var featureCatalogComponent: FeatureCatalogComponent

    override fun onCreate() {
        super.onCreate()

        val appDeps = AppDeps.create()

        val networkComponent = DaggerNetworkComponent.builder()
            .applicationContext(this)
            .buildConfigProvider(appDeps.buildConfigProvider)
            .build()

        coffeeShopAppComponent = DaggerCoffeeShopAppComponent.builder()
            .applicationContext(this)
            .build()

        featureCatalogComponent = DaggerFeatureCatalogComponent.builder()
            .okHttpClient(networkComponent.okHttpClient)
            .buildConfigProvider(appDeps.buildConfigProvider)
            .build()
    }
}

fun Context.coffeeShopAppComponent(): CoffeeShopAppComponent {
    return when (this) {
        is CoffeeShopApp -> coffeeShopAppComponent
        else -> applicationContext.coffeeShopAppComponent()
    }
}

fun Context.featureCatalogComponent(): FeatureCatalogComponent {
    return when (this) {
        is CoffeeShopApp -> featureCatalogComponent
        else -> applicationContext.featureCatalogComponent()
    }
}
