package com.coffeeshop.coffeeshopapp

import android.app.Application
import android.content.Context
import com.coffeeshop.auth.internal.di.DaggerFeatureAuthComponent
import com.coffeeshop.auth.internal.di.FeatureAuthComponent
import com.coffeeshop.cache.internal.di.CoreCacheComponent
import com.coffeeshop.cart.internal.di.DaggerFeatureCartComponent
import com.coffeeshop.cart.internal.di.FeatureCartComponent
import com.coffeeshop.database.di.DaggerDatabaseComponent
import com.coffeeshop.database.di.DatabaseComponent
import com.coffeeshop.di.CoreDiComponent
import com.coffeeshop.di.DaggerCoreDiComponent
import com.coffeeshop.json.JsonComponent
import com.coffeeshop.network.di.DaggerNetworkComponent
import com.coffeeshop.network.di.NetworkComponent
import com.coffeeshop.product_detail.internal.di.DaggerFeatureProductDetailComponent
import com.coffeeshop.product_detail.internal.di.FeatureProductDetailComponent
import com.coffeeshop.profile.internal.di.DaggerFeatureProfileComponent
import com.coffeeshop.profile.internal.di.FeatureProfileComponent
import com.coffeshop.catalog.internal.di.DaggerFeatureCatalogComponent
import com.coffeshop.catalog.internal.di.FeatureCatalogComponent
import com.coffeshop.deps.AppDeps
import com.coffeshop.navigation.di.CoreNavigationComponent

class CoffeeShopApp : Application() {

    private val appDeps by lazy { AppDeps.create() }


    internal lateinit var jsonComponent: JsonComponent
    internal lateinit var coreDiComponent: CoreDiComponent
    internal lateinit var coreNavigationComponent: CoreNavigationComponent
    internal lateinit var coreCacheComponent: CoreCacheComponent
    internal lateinit var databaseComponent: DatabaseComponent
    internal lateinit var networkComponent: NetworkComponent


    internal lateinit var coffeeShopAppComponent: CoffeeShopAppComponent


    internal lateinit var featureAuthComponent: FeatureAuthComponent
    internal lateinit var featureCatalogComponent: FeatureCatalogComponent
    internal lateinit var featureProfileComponent: FeatureProfileComponent
    internal lateinit var featureProductDetailComponent: FeatureProductDetailComponent
    internal lateinit var featureCartComponent: FeatureCartComponent


    override fun onCreate() {
        super.onCreate()

        jsonComponent = JsonComponent.get

        coreDiComponent = DaggerCoreDiComponent.create()

        coreNavigationComponent = appDeps.coreNavigationComponent

        coreCacheComponent = appDeps.coreCacheComponent

        databaseComponent = DaggerDatabaseComponent.builder()
            .applicationContext(this)
            .coreDiComponent(coreDiComponent)
            .jsonComponent(jsonComponent)
            .build()

        networkComponent = DaggerNetworkComponent.builder()
            .applicationContext(this)
            .buildConfigProvider(appDeps.buildConfigProvider)
            .coreDiComponent(coreDiComponent)
            .jsonComponent(jsonComponent)
            .build()


        coffeeShopAppComponent = DaggerCoffeeShopAppComponent.builder()
            .applicationContext(this)
            .appDeps(appDeps)
            .build()


        featureCartComponent = DaggerFeatureCartComponent.builder()
            .networkComponent(networkComponent)
            .databaseComponent(databaseComponent)
            .coreDiComponent(coreDiComponent)
            .jsonComponent(jsonComponent)
            .router(coreNavigationComponent.router())
            .logger(appDeps.logger)
            .build()

        featureAuthComponent = DaggerFeatureAuthComponent.builder()
            .retrofit(networkComponent.retrofit)
            .dispatcherIo(coreDiComponent.dispatcherIO)
            .router(coreNavigationComponent.router())
            .build()

        featureCatalogComponent = DaggerFeatureCatalogComponent.builder()
            .getTotalPriceFromCart(featureCartComponent.getTotalPriceFromCartUseCase)
            .jsonComponent(jsonComponent)
            .networkComponent(networkComponent)
            .databaseComponent(databaseComponent)
            .coreDiComponent(coreDiComponent)
            .router(coreNavigationComponent.router())
            .buildConfigProvider(appDeps.buildConfigProvider)
            .logger(appDeps.logger)
            .productDetailInMemoryCache(coreCacheComponent.productDetailCache())
            .build()

        featureProfileComponent = DaggerFeatureProfileComponent.builder()
            .coreDiComponent(coreDiComponent)
            .applicationContext(this)
            .retrofit(networkComponent.retrofit)
            .router(coreNavigationComponent.router())
            .build()

        featureProductDetailComponent = DaggerFeatureProductDetailComponent.builder()
            .coreDiComponent(coreDiComponent)
            .router(coreNavigationComponent.router())
            .logger(appDeps.logger)
            .productDetailInMemoryCache(coreCacheComponent.productDetailCache())
            .getProductDetailFromCacheUseCase(featureCatalogComponent.getProductDetailFromCacheUseCase)
            .removeProductDetailFromCacheUseCase(featureCatalogComponent.removeProductDetailFromCacheUseCase)
            .addToCartUseCase(featureCartComponent.addToCartUseCase)
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

fun Context.featureAuthComponent(): FeatureAuthComponent {
    return when(this) {
        is CoffeeShopApp -> featureAuthComponent
        else -> applicationContext.featureAuthComponent()
    }
}

fun Context.featureProfileComponent(): FeatureProfileComponent {
    return when(this) {
        is CoffeeShopApp -> featureProfileComponent
        else -> applicationContext.featureProfileComponent()
    }
}

fun Context.featureProductDetail(): FeatureProductDetailComponent {
    return when(this) {
        is CoffeeShopApp -> featureProductDetailComponent
        else -> applicationContext.featureProductDetail()
    }
}

fun Context.featureCart(): FeatureCartComponent {
    return when(this) {
        is CoffeeShopApp -> featureCartComponent
        else -> applicationContext.featureCart()
    }
}