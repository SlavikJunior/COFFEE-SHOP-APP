package com.coffeeshop.auth.api.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.coffeshop.navigation.FeatureEntryBuilder

interface RegisterEntryBuilder : FeatureEntryBuilder<Route.RegisterScreen> {

    override fun build(scope: EntryProviderScope<Route.RegisterScreen>)
}