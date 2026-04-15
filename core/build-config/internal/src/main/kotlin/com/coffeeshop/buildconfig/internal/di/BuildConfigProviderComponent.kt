package com.coffeeshop.buildconfig.internal.di

import com.coffeeshop.buildconfig.api.BuildConfigProvider
import dagger.Component

@Component(modules = [BuildConfigProviderModule::class])
@BuildConfigProviderScope
interface BuildConfigProviderComponent {

    val buildConfigProvider: BuildConfigProvider
}