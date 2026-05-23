package com.coffeeshop.auth.internal.navigation

import androidx.lifecycle.ViewModelProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.arttttt.nav3router.Router
import com.coffeeshop.auth.api.presentation.navigation.LoginRoute
import com.coffeeshop.auth.api.presentation.navigation.RegisterRoute
import com.coffeeshop.auth.internal.screen.login.LoginScreen
import com.coffeeshop.auth.internal.screen.register.RegisterScreen
import com.coffeeshop.di.qualifiers.LoginViewModelFactory
import com.coffeeshop.di.qualifiers.RegisterViewModelFactory
import com.coffeshop.navigation.Route

fun EntryProviderScope<NavKey>.loginScreenEntry(
    router: Router<Route>,
    @LoginViewModelFactory viewModelFactory: ViewModelProvider.Factory
) {
    entry<LoginRoute> {
        LoginScreen(
            router = router,
            viewModelFactory
        )
    }
}

fun EntryProviderScope<NavKey>.registerScreenEntry(
    router: Router<Route>,
    @RegisterViewModelFactory viewModelFactory: ViewModelProvider.Factory
) {
    entry<RegisterRoute> {
        RegisterScreen(
            router = router,
            viewModelFactory
        )
    }
}
