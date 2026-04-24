package com.coffeeshop.coffeeshopapp

import android.app.Application
import android.content.Context
import com.coffeeshop.di.CoreDiComponent
import com.coffeeshop.network.di.DaggerNetworkComponent
import com.coffeshop.catalog.internal.di.DaggerFeatureCatalogComponent
import com.coffeshop.catalog.internal.di.FeatureCatalogComponent
import com.coffeshop.deps.AppDeps

class CoffeeShopApp : Application() {

    internal lateinit var coffeeShopAppComponent: CoffeeShopAppComponent

//    internal lateinit var featureAuthComponent: FeatureAuthComponent

    internal lateinit var featureCatalogComponent: FeatureCatalogComponent

    override fun onCreate() {
        super.onCreate()

        val appDeps = AppDeps.create()
//        val authDeps = FeatureAuthDeps.create()

        val networkComponent = DaggerNetworkComponent.builder()
            .applicationContext(this)
            .buildConfigProvider(appDeps.buildConfigProvider)
            .coreDiComponent(CoreDiComponent.get())
            .build()

        coffeeShopAppComponent = DaggerCoffeeShopAppComponent.builder()
            .applicationContext(this)
            .build()

//        featureAuthComponent = DaggerFeatureAuthComponent.factory().create(authDeps)

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
