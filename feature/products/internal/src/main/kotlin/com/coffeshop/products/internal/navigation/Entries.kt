package com.coffeshop.products.internal.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arttttt.nav3router.Router
import com.coffeshop.navigation.Route
import com.coffeshop.products.api.presentation.navigation.ProductsRoute as CatalogRoute
import com.coffeshop.products.internal.screen.catalog.CatalogScreen

fun EntryProviderScope<NavKey>.catalogScreenEntry(
    router: Router<Route>,
) {
    entry<CatalogRoute> {
        CatalogScreen(
            onProfileClick = { /* TODO: navigate to profile */ },
            onProductClick = { /* TODO: navigate to product detail */ },
        )
    }
}
