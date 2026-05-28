package com.github.slavikjunior.favorites.internal.navigation

import androidx.lifecycle.ViewModelProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.slavikjunior.favorites.api.navigation.FavoritesRoute
import com.github.slavikjunior.favorites.internal.screen.favorites.FavoritesScreen

fun EntryProviderScope<NavKey>.favoritesEntry(viewModelFactory: ViewModelProvider.Factory) {
    entry<FavoritesRoute> {
        FavoritesScreen(viewModelFactory = viewModelFactory)
    }
}
