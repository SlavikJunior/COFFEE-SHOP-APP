package com.coffeeshop.auth.internal.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.coffeeshop.auth.api.presentation.navigation.RegisterEntryBuilder
import com.coffeeshop.auth.api.presentation.navigation.Route
import com.coffeshop.navigation.Navigator
import com.coffeeshop.auth.internal.screen.register.RegisterScreen

internal class RegisterEntryBuilderImpl(private val navigator: Navigator<Route>) : RegisterEntryBuilder {

    override fun build(scope: EntryProviderScope<Route.RegisterScreen>) {
        with(scope) {
            entry<Route.RegisterScreen> {
                RegisterScreen(onNavigateToLogin = { navigator.popUpTo(Route.LoginScreen) })
            }
        }
    }
}