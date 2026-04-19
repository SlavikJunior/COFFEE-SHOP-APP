package com.coffeshop.products.internal.navigation

import androidx.lifecycle.ViewModelProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arttttt.nav3router.Router
import com.coffeeshop.profile.api.presentation.navigation.ProfileRoute
import com.coffeshop.navigation.Route
import com.coffeshop.products.api.presentation.navigation.CatalogRoute as CatalogRoute
import com.coffeshop.products.internal.screen.catalog.MyCatalogScreen

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