package com.coffeeshop.profile.api.presentation.navigation

import com.coffeshop.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class ProfileRoute(
    val isLoggedIn: Boolean = false
): Route