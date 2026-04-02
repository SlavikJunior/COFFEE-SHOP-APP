package com.coffeeshop.auth.api.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.coffeshop.navigation.FeatureEntryBuilder

interface LoginEntryBuilder : FeatureEntryBuilder<Route.LoginScreen> {

    override fun build(scope: EntryProviderScope<Route.LoginScreen>)
}