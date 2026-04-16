package com.coffeeshop.di.multibindings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

class MultiBindingFactory
@Inject constructor(
    private val viewModelMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val result: @JvmSuppressWildcards Provider<ViewModel> = viewModelMap[modelClass]
            ?: viewModelMap.entries.firstOrNull { entry -> modelClass.isAssignableFrom(entry.key) }?.value
            ?: throw IllegalStateException("Unknown view model type: ${modelClass.simpleName}")

        return result.get() as T
    }
}