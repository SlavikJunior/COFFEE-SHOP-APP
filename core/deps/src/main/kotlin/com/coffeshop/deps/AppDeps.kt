package com.coffeshop.deps

import com.coffeeshop.buildconfig.internal.di.BuildConfigProviderComponent
import dagger.Component

@Component(dependencies = [BuildConfigProviderComponent::class])
@AppDepsScope
interface AppDeps {
}