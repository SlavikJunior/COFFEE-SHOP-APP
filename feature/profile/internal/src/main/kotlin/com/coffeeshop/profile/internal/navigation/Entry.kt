package com.coffeeshop.profile.internal.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arttttt.nav3router.Router
import com.coffeeshop.profile.api.presentation.navigation.ProfileRoute
import com.coffeeshop.profile.internal.screen.ProfileScreen
import com.coffeshop.navigation.Route

fun EntryProviderScope<NavKey>.profileScreenEntry(
    router: Router<Route>
) {
    entry<ProfileRoute> {
        ProfileScreen()
    }
}