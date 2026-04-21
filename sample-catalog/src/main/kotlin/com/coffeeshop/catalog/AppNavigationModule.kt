package com.coffeeshop.catalog

import com.arttttt.nav3router.Router
import com.coffeshop.navigation.Route
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal object AppNavigationModule {

    @Provides
    @Singleton
    fun provideRouter(): Router<Route> = Router()
}
