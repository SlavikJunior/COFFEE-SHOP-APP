package com.coffeeshop.di

import com.coffeeshop.di.qualifiers.DispatcherDefault
import com.coffeeshop.di.qualifiers.DispatcherIO
import com.coffeeshop.di.qualifiers.DispatcherUnconfined
import dagger.Component
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Component(
    modules = [CoreDiModule::class]
)
@CoreDiScope
interface CoreDiComponent {

    @get:DispatcherIO
    val dispatcherIO: CoroutineDispatcher

    @get:DispatcherDefault
    val dispatcherDefault: CoroutineDispatcher

    @get:DispatcherUnconfined
    val dispatcherUnconfined: CoroutineDispatcher

    companion object {
        fun get(): CoreDiComponent = object : CoreDiComponent {
            override val dispatcherIO: CoroutineDispatcher
                get() = Dispatchers.IO
            override val dispatcherDefault: CoroutineDispatcher
                get() = Dispatchers.Default
            override val dispatcherUnconfined: CoroutineDispatcher
                get() = Dispatchers.Unconfined

        }
    }
}