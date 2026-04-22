package com.coffeshop.catalog.internal.navigation

import androidx.lifecycle.ViewModelProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arttttt.nav3router.Router
import com.coffeeshop.profile.api.presentation.navigation.ProfileRoute
import com.coffeshop.catalog.api.presentation.navigation.CatalogRoute
import com.coffeshop.catalog.internal.screen.catalog.MyCatalogScreen
import com.coffeshop.navigation.Route

fun EntryProviderScope<NavKey>.catalogScreenEntry(
    router: Router<Route>,
    viewModelFactory: ViewModelProvider.Factory,
) {
    entry<CatalogRoute> {
        MyCatalogScreen(
            onError = {  },
            onProfileClick = { router.push(ProfileRoute()) },
            viewModelFactory = viewModelFactory,
        )
    }
}