package com.coffeeshop.orderhistory.internal.di

import androidx.lifecycle.ViewModelProvider
import com.coffeeshop.di.CoreDiComponent
import dagger.BindsInstance
import dagger.Component
import retrofit2.Retrofit

@Component(
    modules = [
        OrderHistoryModule::class,
        OrderHistoryBindingModule::class,
    ],
    dependencies = [CoreDiComponent::class],
)
@OrderHistoryScope
interface FeatureOrderHistoryComponent {

    val viewModelFactory: ViewModelProvider.Factory

    @Component.Builder
    interface Builder {
        fun coreDiComponent(coreDiComponent: CoreDiComponent): Builder

        @BindsInstance
        fun retrofit(retrofit: Retrofit): Builder

        fun build(): FeatureOrderHistoryComponent
    }
}
