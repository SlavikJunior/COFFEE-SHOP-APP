package com.coffeeshop.network.di

import com.coffeeshop.network.TokenRepository
import com.coffeeshop.network.TokenRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
internal interface NetworkBindingModule {

    @[Binds NetworkScope]
    fun bindTokenRepositoryToImpl(impl: TokenRepositoryImpl): TokenRepository
}