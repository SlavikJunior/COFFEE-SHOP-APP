package com.coffeeshop.di

import com.coffeeshop.di.qualifiers.DispatcherDefault
import com.coffeeshop.di.qualifiers.DispatcherIO
import com.coffeeshop.di.qualifiers.DispatcherMain
import com.coffeeshop.di.qualifiers.DispatcherUnconfined
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
object CoreDiModule {

    @[Provides CoreDiScope DispatcherIO]
    fun provideDispatcherIO(): CoroutineDispatcher = Dispatchers.IO

    @[Provides CoreDiScope DispatcherDefault]
    fun provideDispatcherDefault(): CoroutineDispatcher = Dispatchers.Default

    @[Provides CoreDiScope DispatcherUnconfined]
    fun provideDispatcherUnconfined(): CoroutineDispatcher = Dispatchers.Unconfined

    @[Provides CoreDiScope DispatcherMain]
    fun provideDispatcherMain(): CoroutineDispatcher = Dispatchers.Main
}