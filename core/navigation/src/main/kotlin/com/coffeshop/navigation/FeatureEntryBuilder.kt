package com.coffeshop.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

interface FeatureEntryBuilder<T : NavKey> {
    fun build(scope: EntryProviderScope<T>)
}