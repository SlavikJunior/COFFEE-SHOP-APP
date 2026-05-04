package com.coffeshop.catalog.internal.navigation

import androidx.lifecycle.ViewModelProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.coffeshop.catalog.api.presentation.navigation.CatalogRoute
import com.coffeshop.catalog.internal.screen.catalog.CatalogScreen

fun EntryProviderScope<NavKey>.catalogScreenEntry(
    viewModelFactory: ViewModelProvider.Factory,
) {
    entry<CatalogRoute> {
        CatalogScreen(
            viewModelFactory = viewModelFactory,
        )
    }
}