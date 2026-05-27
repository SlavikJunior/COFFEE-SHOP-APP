package com.coffeeshop.activeorders.internal.navigation

import androidx.lifecycle.ViewModelProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.coffeeshop.activeorders.api.presentation.navigation.ActiveOrdersRoute
import com.coffeeshop.activeorders.internal.screen.activeorders.ActiveOrdersScreen

fun EntryProviderScope<NavKey>.activeOrdersEntry(
    viewModelFactory: ViewModelProvider.Factory
) {
    entry<ActiveOrdersRoute> {
        ActiveOrdersScreen(viewModelFactory)
    }
}