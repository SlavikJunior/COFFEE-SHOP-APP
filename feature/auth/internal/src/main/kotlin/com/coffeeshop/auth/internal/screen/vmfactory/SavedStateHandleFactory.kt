package com.coffeeshop.auth.internal.screen.vmfactory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

interface SavedStateHandleFactory<T : ViewModel> {
    fun create(savedStateHandle: SavedStateHandle): T
}