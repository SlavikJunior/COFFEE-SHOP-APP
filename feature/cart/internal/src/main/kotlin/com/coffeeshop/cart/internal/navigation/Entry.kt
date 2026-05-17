package com.coffeeshop.cart.internal.navigation

import androidx.lifecycle.ViewModelProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.coffeeshop.cart.api.presentation.navigation.CartRoute
import com.coffeeshop.cart.internal.screen.cart.CartScreen

fun EntryProviderScope<NavKey>.cartEntry(
    viewModelFactory: ViewModelProvider.Factory
) {
    entry<CartRoute> {
        CartScreen(
            viewModelFactory = viewModelFactory
        )
    }
}