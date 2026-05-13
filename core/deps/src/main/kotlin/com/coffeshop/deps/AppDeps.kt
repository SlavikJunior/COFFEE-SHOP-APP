package com.coffeshop.deps

import com.arttttt.nav3router.Router
import com.coffeeshop.buildconfig.api.BuildConfigProvider
import com.coffeeshop.buildconfig.internal.di.BuildConfigProviderComponent
import com.coffeeshop.buildconfig.internal.di.DaggerBuildConfigProviderComponent
import com.coffeeshop.cache.internal.di.CoreCacheComponent
import com.coffeeshop.cache.internal.di.DaggerCoreCacheComponent
import com.coffeshop.navigation.Route
import com.coffeshop.navigation.di.CoreNavigationComponent
import com.coffeshop.navigation.di.DaggerCoreNavigationComponent
import dagger.Component

@Component(
    dependencies = [
        CoreCacheComponent::class,
        BuildConfigProviderComponent::class,
        CoreNavigationComponent::class
    ]
)
@AppDepsScope
interface AppDeps {
    val coreCacheComponent: CoreCacheComponent

    val buildConfigProvider: BuildConfigProvider

    val coreNavigationComponent: CoreNavigationComponent

    val router: Router<Route>

    @Component.Builder
    interface Builder {
        fun coreCacheComponent(component: CoreCacheComponent): Builder
        fun buildConfigProviderComponent(component: BuildConfigProviderComponent): Builder
        fun coreNavigationComponent(component: CoreNavigationComponent): Builder
        fun build(): AppDeps
    }

    companion object {
        fun create(): AppDeps {
            return DaggerAppDeps.builder()
                .coreCacheComponent(DaggerCoreCacheComponent.create())
                .buildConfigProviderComponent(DaggerBuildConfigProviderComponent.create())
                .coreNavigationComponent(DaggerCoreNavigationComponent.create())
                .build()
        }
    }
}