package com.coffeeshop.auth.api.presentation.navigation

import androidx.navigation3.runtime.NavKey

sealed interface Route: NavKey {
    data object RegisterScreen : Route
    data object LoginScreen : Route
}