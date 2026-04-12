package com.coffeeshop.auth.internal.di

import dagger.Component

@AuthScope
@Component(
    modules = [FeatureAuthModule::class],
    dependencies = [FeatureAuthDeps::class]
)
internal interface FeatureAuthComponent {

    @Component.Factory
    interface Factory {
        fun create(
            deps: FeatureAuthDeps
        ): FeatureAuthComponent
    }
}