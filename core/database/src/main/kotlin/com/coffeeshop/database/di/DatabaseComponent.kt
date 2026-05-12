package com.coffeeshop.database.di

import android.content.Context
import com.coffeeshop.database.common.CoffeeShopDatabase
import com.coffeeshop.di.CoreDiComponent
import com.coffeeshop.di.qualifiers.ApplicationContext
import com.coffeeshop.json.JsonComponent
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [DatabaseModule::class],
    dependencies = [
        CoreDiComponent::class,
        JsonComponent::class
    ]
)
@DatabaseScope
interface DatabaseComponent {

    fun coffeeShopDatabase(): CoffeeShopDatabase

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun applicationContext(@ApplicationContext context: Context): Builder

        fun coreDiComponent(coreDiComponent: CoreDiComponent): Builder

        fun jsonComponent(jsonComponent: JsonComponent): Builder

        fun build(): DatabaseComponent
    }
}