package com.coffeeshop.buildconfig.internal.di

import com.coffeeshop.buildconfig.api.BuildConfigProvider
import com.coffeeshop.buildconfig.internal.BuildConfigProviderImpl
import dagger.Binds
import dagger.Module

@Module
internal interface BuildConfigProviderModule {

    @Binds
    fun bindBuildConfigProvider(target: BuildConfigProviderImpl): BuildConfigProvider
}