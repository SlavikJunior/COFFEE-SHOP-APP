package com.coffeeshop.auth.internal.screen.vmfactory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

class UltimateAssistedFactory
@Inject constructor(
    private val viewModelMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: KClass<T>,
        extras: CreationExtras
    ): T {
        val result: @JvmSuppressWildcards Provider<ViewModel> = viewModelMap[modelClass.java]
            ?: viewModelMap.entries.firstOrNull { entry -> modelClass.java.isAssignableFrom(entry.key) }?.value
            ?: throw IllegalStateException("Unknown view model type: ${modelClass.simpleName}")

        return result.get() as T
    }
}

interface SavedStateHandleFactory<T : ViewModel> {
    fun create(savedStateHandle: SavedStateHandle): T
}