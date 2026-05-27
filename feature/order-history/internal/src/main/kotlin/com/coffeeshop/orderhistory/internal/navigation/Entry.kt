package com.coffeeshop.orderhistory.internal.navigation

import androidx.lifecycle.ViewModelProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.coffeeshop.orderhistory.api.presentation.navigation.OrderHistoryRoute
import com.coffeeshop.orderhistory.internal.screen.OrderHistoryScreen
import com.coffeshop.navigation.bottomSheetMetadata

fun EntryProviderScope<NavKey>.orderHistoryEntry(
    viewModelFactory: ViewModelProvider.Factory,
) {
    entry<OrderHistoryRoute>(
        clazzContentKey = { it::class.toString() },
        metadata = bottomSheetMetadata(),
    ) {
        OrderHistoryScreen(viewModelFactory = viewModelFactory)
    }
}
