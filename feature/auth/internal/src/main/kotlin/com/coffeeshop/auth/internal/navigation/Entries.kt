package com.coffeeshop.auth.internal.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.arttttt.nav3router.Router
import com.coffeeshop.auth.api.presentation.navigation.LoginRoute
import com.coffeeshop.auth.api.presentation.navigation.RegisterRoute
import com.coffeeshop.auth.internal.screen.login.LoginScreen
import com.coffeeshop.auth.internal.screen.register.RegisterScreen
import com.coffeshop.navigation.Route

fun EntryProviderScope<NavKey>.loginScreenEntry(
    router: Router<Route>
) {
    entry<LoginRoute> {
        LoginScreen(
            router = router
        )
    }
}

fun EntryProviderScope<NavKey>.registerScreenEntry(
    router: Router<Route>
) {
    entry<RegisterRoute> {
        RegisterScreen(
            router = router
        )
    }
}
