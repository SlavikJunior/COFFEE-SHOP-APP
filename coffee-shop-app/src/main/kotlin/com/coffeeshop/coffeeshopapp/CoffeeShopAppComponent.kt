package com.coffeeshop.coffeeshopapp

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppNavigationModule::class])
interface CoffeeShopAppComponent {

    fun inject(activity: MainActivity)
}