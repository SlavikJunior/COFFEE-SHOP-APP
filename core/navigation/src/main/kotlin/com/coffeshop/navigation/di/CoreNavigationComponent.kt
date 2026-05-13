package com.coffeshop.navigation.di

import com.arttttt.nav3router.Router
import com.coffeshop.navigation.Route
import dagger.Component

@Component(
    modules = [CoreNavigationModule::class]
)
@CoreNavigationScope
interface CoreNavigationComponent {

    fun router(): Router<Route>
}