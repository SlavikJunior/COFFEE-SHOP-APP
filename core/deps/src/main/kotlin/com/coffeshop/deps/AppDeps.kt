package com.coffeshop.deps

import com.arttttt.nav3router.Router
import com.coffeeshop.buildconfig.api.BuildConfigProvider
import com.coffeeshop.buildconfig.internal.di.BuildConfigProviderComponent
import com.coffeeshop.buildconfig.internal.di.DaggerBuildConfigProviderComponent
import com.coffeeshop.cache.internal.di.CoreCacheComponent
import com.coffeeshop.cache.internal.di.DaggerCoreCacheComponent
import com.coffeeshop.logger.api.CoffeeshopLogger
import com.coffeeshop.logger.internal.di.CoffeeshopLoggerComponent
import com.coffeeshop.logger.internal.di.DaggerCoffeeshopLoggerComponent
import com.coffeshop.navigation.Route
import com.coffeshop.navigation.di.CoreNavigationComponent
import com.coffeshop.navigation.di.DaggerCoreNavigationComponent
import dagger.Component

@Component(
    dependencies = [
        CoreCacheComponent::class,
        BuildConfigProviderComponent::class,
        CoreNavigationComponent::class,
        CoffeeshopLoggerComponent::class
    ]
)
@AppDepsScope
interface AppDeps {
    val coreCacheComponent: CoreCacheComponent

    val buildConfigProvider: BuildConfigProvider

    val coreNavigationComponent: CoreNavigationComponent

    val router: Router<Route>

    val logger: CoffeeshopLogger

    @Component.Builder
    interface Builder {
        fun coffeeshopLoggerComponent(component: CoffeeshopLoggerComponent): Builder
        fun coreCacheComponent(component: CoreCacheComponent): Builder
        fun buildConfigProviderComponent(component: BuildConfigProviderComponent): Builder
        fun coreNavigationComponent(component: CoreNavigationComponent): Builder
        fun build(): AppDeps
    }

    companion object {
        fun create(): AppDeps {
            val loggerComponent = DaggerCoffeeshopLoggerComponent.create()

            return DaggerAppDeps.builder()
                .coffeeshopLoggerComponent(loggerComponent)
                .coreCacheComponent(DaggerCoreCacheComponent.builder().logger(loggerComponent.coffeeshopLogger()).build())
                .buildConfigProviderComponent(DaggerBuildConfigProviderComponent.create())
                .coreNavigationComponent(DaggerCoreNavigationComponent.create())
                .build()
        }
    }
}