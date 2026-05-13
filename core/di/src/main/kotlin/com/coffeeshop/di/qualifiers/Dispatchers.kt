package com.coffeeshop.di.qualifiers

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DispatcherIO

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DispatcherDefault

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DispatcherUnconfined

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DispatcherMain