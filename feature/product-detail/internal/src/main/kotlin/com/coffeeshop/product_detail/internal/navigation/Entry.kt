package com.coffeeshop.product_detail.internal.navigation

import androidx.lifecycle.ViewModelProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arttttt.nav3router.Router
import com.coffeeshop.product_detail.api.presentation.navigation.ProductDetailRoute
import com.coffeeshop.product_detail.internal.screen.product_detail.ProductDetailScreen
import com.coffeshop.navigation.Route

fun EntryProviderScope<NavKey>.productDetailScreenEntry(
    viewModelFactory: ViewModelProvider.Factory
) {
    entry<ProductDetailRoute> { route ->
        ProductDetailScreen(
            productId = route.productID,
            viewModelFactory = viewModelFactory,
        )
    }
}