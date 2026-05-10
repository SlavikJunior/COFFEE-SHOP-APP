package com.coffeeshop.di.qualifiers

import javax.inject.Qualifier

@Qualifier
annotation class InMemoryCache

@Qualifier
annotation class RoomCache

@Qualifier
annotation class SharedPreferencesCache