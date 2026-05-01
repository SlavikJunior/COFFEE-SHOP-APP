package com.coffeshop.deps

import com.arttttt.nav3router.Router
import com.coffeeshop.buildconfig.api.BuildConfigProvider
import com.coffeeshop.buildconfig.internal.di.BuildConfigProviderComponent
import com.coffeeshop.buildconfig.internal.di.DaggerBuildConfigProviderComponent
import com.coffeshop.navigation.Route
import com.coffeshop.navigation.di.CoreNavigationComponent
import com.coffeshop.navigation.di.DaggerCoreNavigationComponent
import dagger.Component

@Component(
    dependencies = [
        BuildConfigProviderComponent::class,
        CoreNavigationComponent::class
    ]
)
@AppDepsScope
interface AppDeps {

    val buildConfigProvider: BuildConfigProvider

    val coreNavigationComponent: CoreNavigationComponent

    val router: Router<Route>

    @Component.Builder
    interface Builder {
        fun buildConfigProviderComponent(component: BuildConfigProviderComponent): Builder
        fun coreNavigationComponent(component: CoreNavigationComponent): Builder
        fun build(): AppDeps
    }

    companion object {
        fun create(): AppDeps {
            return DaggerAppDeps.builder()
                .buildConfigProviderComponent(DaggerBuildConfigProviderComponent.create())
                .coreNavigationComponent(DaggerCoreNavigationComponent.create())
                .build()
        }
    }
}