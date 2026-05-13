package com.coffeshop.navigation.di

import com.arttttt.nav3router.Router
import com.coffeshop.navigation.Route
import dagger.Module
import dagger.Provides

@Module
internal object CoreNavigationModule {

    @[Provides CoreNavigationScope]
    fun provideRouter(): Router<Route> = Router()
}