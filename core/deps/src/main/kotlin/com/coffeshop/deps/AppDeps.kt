package com.coffeshop.deps

import com.coffeeshop.buildconfig.api.BuildConfigProvider
import com.coffeeshop.buildconfig.internal.di.BuildConfigProviderComponent
import com.coffeeshop.buildconfig.internal.di.DaggerBuildConfigProviderComponent
import dagger.Component

@Component(dependencies = [BuildConfigProviderComponent::class])
@AppDepsScope
interface AppDeps {

    val buildConfigProvider: BuildConfigProvider

    @Component.Builder
    interface Builder {
        fun buildConfigProviderComponent(component: BuildConfigProviderComponent): Builder
        fun build(): AppDeps
    }

    companion object {
        fun create(): AppDeps {
            return DaggerAppDeps.builder()
                .buildConfigProviderComponent(DaggerBuildConfigProviderComponent.create())
                .build()
        }
    }
}