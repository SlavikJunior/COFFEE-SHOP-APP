package com.coffeeshop.auth.api.presentation.navigation

import com.coffeshop.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRoute(
    val phone: String = "",
    val name: String = ""
) : Route

@Serializable
data class LoginRoute(
    val phone: String = "",
    val message: String? = null
) : Route