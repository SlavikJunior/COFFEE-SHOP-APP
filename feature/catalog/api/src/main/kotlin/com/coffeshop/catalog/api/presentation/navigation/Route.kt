package com.coffeshop.catalog.api.presentation.navigation

import com.coffeshop.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class CatalogRoute(
    val isLoggedIn: Boolean = true,
    val isRetryAfterError: Boolean = false
) : Route
