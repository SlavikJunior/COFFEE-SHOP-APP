package com.coffeeshop.logger.internal.di

import com.coffeeshop.logger.api.CoffeeshopLogger
import com.coffeeshop.logger.internal.impl.AndroidCoffeeshopLoggerImpl
import dagger.Binds
import dagger.Component
import dagger.Module
import javax.inject.Scope

@Component(
    modules = [LoggerModule::class]
)
@LoggerScope
interface CoffeeshopLoggerComponent {

    fun coffeeshopLogger(): CoffeeshopLogger
}

@Scope
internal annotation class LoggerScope

@Module
internal interface LoggerModule {

    @[Binds LoggerScope]
    fun bindCoffeeshopLoggerToImpl(impl: AndroidCoffeeshopLoggerImpl): CoffeeshopLogger
}