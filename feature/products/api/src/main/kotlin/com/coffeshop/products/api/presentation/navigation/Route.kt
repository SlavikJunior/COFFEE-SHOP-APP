package com.coffeshop.products.api.presentation.navigation

import com.coffeshop.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class ProductsRoute(
    val isLoggedIn: Boolean = true
) : Route
