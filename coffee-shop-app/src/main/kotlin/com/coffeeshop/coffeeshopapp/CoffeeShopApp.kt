package com.coffeeshop.coffeeshopapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.coffeeshop.activeorders.internal.di.DaggerFeatureActiveOrdersComponent
import com.coffeeshop.activeorders.internal.di.FeatureActiveOrdersComponent
import com.coffeeshop.auth.internal.di.DaggerFeatureAuthComponent
import com.github.slavikjunior.favorites.internal.di.DaggerFeatureFavoritesComponent
import com.github.slavikjunior.favorites.internal.di.FeatureFavoritesComponent
import com.coffeeshop.coffeeshopapp.permissions.notificationsPermissionState
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
import com.coffeeshop.orderhistory.internal.di.DaggerFeatureOrderHistoryComponent
import com.coffeeshop.orderhistory.internal.di.FeatureOrderHistoryComponent
import com.coffeeshop.product_detail.internal.di.DaggerFeatureProductDetailComponent
import com.coffeeshop.product_detail.internal.di.FeatureProductDetailComponent
import com.coffeeshop.profile.internal.di.DaggerFeatureProfileComponent
import com.coffeeshop.profile.internal.di.FeatureProfileComponent
import com.coffeshop.catalog.internal.di.DaggerFeatureCatalogComponent
import com.coffeshop.catalog.internal.di.FeatureCatalogComponent
import com.coffeeshop.auth.api.presentation.navigation.LoginRoute
import com.coffeshop.deps.AppDeps
import com.coffeshop.navigation.di.CoreNavigationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CoffeeShopApp : Application() {

    private val appDeps by lazy { AppDeps.create() }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)


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
    internal lateinit var featureActiveOrdersComponent: FeatureActiveOrdersComponent
    internal lateinit var featureOrderHistoryComponent: FeatureOrderHistoryComponent
    internal lateinit var featureFavoritesComponent: FeatureFavoritesComponent


    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()

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

        navigateToLoginWhenRequired()

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                applicationScope.launch {
                    notificationsPermissionState().refresh(this@CoffeeShopApp)
                }
            }
        })

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
            .tokenRepository(networkComponent.tokenRepository)
            .notificationsRepository(networkComponent.notificationsRepository)
            .build()

        featureCatalogComponent = DaggerFeatureCatalogComponent.builder()
            .isUserLoggedIn(featureAuthComponent.isUserLoggedInUseCase)
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
            .isUserLoggedIn(featureAuthComponent().isUserLoggedInUseCase)
            .coreDiComponent(coreDiComponent)
            .retrofit(networkComponent.retrofit)
            .router(coreNavigationComponent.router())
            .tokenRepository(networkComponent.tokenRepository)
            .notificationsRepository(networkComponent.notificationsRepository)
            .build()

        featureOrderHistoryComponent = DaggerFeatureOrderHistoryComponent.builder()
            .coreDiComponent(coreDiComponent)
            .retrofit(networkComponent.retrofit)
            .build()

        featureActiveOrdersComponent = DaggerFeatureActiveOrdersComponent.builder()
            .router(coreNavigationComponent.router())
            .retrofit(networkComponent.retrofit)
            .coreDiComponent(coreDiComponent)
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

        featureFavoritesComponent = DaggerFeatureFavoritesComponent.builder()
            .databaseComponent(databaseComponent)
            .coreDiComponent(coreDiComponent)
            .isUserLoggedIn(featureAuthComponent.isUserLoggedInUseCase)
            .getTotalPriceFromCart(featureCartComponent.getTotalPriceFromCartUseCase)
            .buildConfigProvider(appDeps.buildConfigProvider)
            .logger(appDeps.logger)
            .router(coreNavigationComponent.router())
            .productDetailInMemoryCache(coreCacheComponent.productDetailCache())
            .getProductDetailByProductId(featureCatalogComponent.getProductDetailByProductIdUseCase)
            .isProductDetailStoredInCache(featureCatalogComponent.isProductDetailStoredInCacheUseCase)
            .saveProductDetailInCache(featureCatalogComponent.saveProductDetailInCacheUseCase)
            .build()
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(NotificationManager::class.java)
        listOf(
            NotificationChannel(CHANNEL_ORDER_STATUS, CHANNEL_ORDER_STATUS_NAME, NotificationManager.IMPORTANCE_DEFAULT),
            NotificationChannel(CHANNEL_AUTH, CHANNEL_AUTH_NAME, NotificationManager.IMPORTANCE_HIGH),
            NotificationChannel(CHANNEL_PROMO, CHANNEL_PROMO_NAME, NotificationManager.IMPORTANCE_DEFAULT),
            NotificationChannel(CHANNEL_DEFAULT, CHANNEL_DEFAULT_NAME, NotificationManager.IMPORTANCE_DEFAULT),
        ).forEach { manager.createNotificationChannel(it) }
    }

    private fun navigateToLoginWhenRequired() {
        applicationScope.launch {
            networkComponent.sessionExpiredFlow.collect {
                coreNavigationComponent.router().replaceCurrent(LoginRoute())
            }
        }
    }

    companion object {
        const val CHANNEL_PROMO = "channel_promo"
        const val CHANNEL_PROMO_NAME = "Промо-акции"
        const val CHANNEL_AUTH = "channel_auth"
        const val CHANNEL_AUTH_NAME = "Безопасность"
        const val CHANNEL_ORDER_STATUS = "channel_order_status"
        const val CHANNEL_ORDER_STATUS_NAME = "Статусы заказов"
        const val CHANNEL_DEFAULT = "channel_default"
        const val CHANNEL_DEFAULT_NAME = "Общие"
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

fun Context.featureActiveOrders(): FeatureActiveOrdersComponent {
    return when(this) {
        is CoffeeShopApp -> featureActiveOrdersComponent
        else -> applicationContext.featureActiveOrders()
    }
}

fun Context.featureOrderHistory(): FeatureOrderHistoryComponent {
    return when(this) {
        is CoffeeShopApp -> featureOrderHistoryComponent
        else -> applicationContext.featureOrderHistory()
    }
}

fun Context.featureFavorites(): FeatureFavoritesComponent {
    return when(this) {
        is CoffeeShopApp -> featureFavoritesComponent
        else -> applicationContext.featureFavorites()
    }
}