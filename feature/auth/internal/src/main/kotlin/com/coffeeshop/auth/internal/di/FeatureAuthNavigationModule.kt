package com.coffeeshop.auth.internal.di

import com.coffeeshop.auth.api.presentation.navigation.LoginEntryBuilder
import com.coffeeshop.auth.api.presentation.navigation.RegisterEntryBuilder
import com.coffeeshop.auth.internal.navigation.LoginEntryBuilderImpl
import com.coffeeshop.auth.internal.navigation.RegisterEntryBuilderImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
internal interface FeatureAuthNavigationModule {

    @Binds
    @IntoSet
    fun bindLoginEntryBuilder(builder: LoginEntryBuilderImpl): LoginEntryBuilder

    @Binds
    @IntoSet
    fun bindRegisterEntryBuilder(builder: RegisterEntryBuilderImpl): RegisterEntryBuilder
}