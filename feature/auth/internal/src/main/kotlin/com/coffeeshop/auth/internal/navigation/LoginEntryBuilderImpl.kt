package com.coffeeshop.auth.internal.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.coffeeshop.auth.api.presentation.navigation.LoginEntryBuilder
import com.coffeeshop.auth.api.presentation.navigation.Route
import com.coffeshop.navigation.Navigator
import com.coffeeshop.auth.internal.screen.login.LoginScreen

internal class LoginEntryBuilderImpl(private val navigator: Navigator<Route>) : LoginEntryBuilder {
    override fun build(scope: EntryProviderScope<Route.LoginScreen>) {
        with(scope) {
            entry<Route.LoginScreen> {
                LoginScreen(onNavigateToRegister = { navigator.navigateTo(Route.RegisterScreen) })
            }
        }
    }
}