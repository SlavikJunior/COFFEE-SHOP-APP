package com.coffeeshop.auth.internal.screen.vmfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

/**
 * ViewModelProvider.Factory для ViewModel'ей, требующих SavedStateHandle через @AssistedInject.
 * Использует Map из Dagger multibindings: Class<ViewModel> → SavedStateHandleFactory<ViewModel>.
 * Передаётся через Entries в экраны, где DI-граф полностью настроен.
 */
class AssistedSavedStateViewModelFactory
@Inject constructor(
    private val factoryMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<SavedStateHandleFactory<out ViewModel>>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        val factoryProvider = factoryMap[modelClass.java]
            ?: factoryMap.entries
                .firstOrNull { (key, _) -> modelClass.java.isAssignableFrom(key) }
                ?.value
            ?: throw IllegalStateException("No factory registered for ${modelClass.simpleName}")

        val savedStateHandle = extras.createSavedStateHandle()
        return factoryProvider.get().create(savedStateHandle) as T
    }
}
