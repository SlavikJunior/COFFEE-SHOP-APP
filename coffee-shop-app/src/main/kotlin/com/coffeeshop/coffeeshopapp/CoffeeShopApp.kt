package com.coffeeshop.coffeeshopapp

import android.app.Application
import android.content.Context

class CoffeeShopApp: Application() {

    internal lateinit var coffeeShopAppComponent: CoffeeShopAppComponent

    override fun onCreate() {
        super.onCreate()

        coffeeShopAppComponent = DaggerCoffeeShopAppComponent.builder()
            .applicationContext(this)
            .build()
    }
}

fun Context.coffeeShopAppComponent(): CoffeeShopAppComponent {
    return when (this) {
        is CoffeeShopApp -> coffeeShopAppComponent
        else -> applicationContext.coffeeShopAppComponent()
    }
}