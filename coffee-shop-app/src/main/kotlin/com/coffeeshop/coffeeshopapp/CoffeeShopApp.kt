package com.coffeeshop.coffeeshopapp

import android.app.Application

class CoffeeShopApp: Application() {

    internal lateinit var coffeeShopAppComponent: CoffeeShopAppComponent

    override fun onCreate() {
        super.onCreate()

        coffeeShopAppComponent = DaggerCoffeeShopAppComponent.create()
    }
}