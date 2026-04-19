package com.coffeshop.products.internal.di

import androidx.lifecycle.ViewModelProvider
import com.coffeeshop.buildconfig.api.BuildConfigProvider
import dagger.BindsInstance
import dagger.Component
import okhttp3.OkHttpClient

@Component(
    modules = [
        FeatureCatalogModule::class,
        FeatureCatalogBindingModule::class
    ]
)
@CatalogScope
interface FeatureCatalogComponent {

    val viewModelFactory: ViewModelProvider.Factory

    @Component.Builder
    interface Builder {
        @BindsInstance fun okHttpClient(client: OkHttpClient): Builder
        @BindsInstance fun buildConfigProvider(provider: BuildConfigProvider): Builder
        fun build(): FeatureCatalogComponent
    }
}