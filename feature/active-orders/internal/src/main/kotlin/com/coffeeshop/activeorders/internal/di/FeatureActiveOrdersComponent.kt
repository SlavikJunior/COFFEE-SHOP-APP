package com.coffeeshop.activeorders.internal.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arttttt.nav3router.Router
import com.coffeeshop.activeorders.api.domain.repository.ActiveOrdersRepository
import com.coffeeshop.activeorders.api.domain.usecase.GetActiveOrdersUseCase
import com.coffeeshop.activeorders.internal.data.repository.ActiveOrdersRepositoryImpl
import com.coffeeshop.activeorders.internal.data.service.ActiveOrdersService
import com.coffeeshop.activeorders.internal.domain.usecase.GetActiveOrdersUseCaseImpl
import com.coffeeshop.activeorders.internal.screen.activeorders.ActiveOrdersViewModel
import com.coffeeshop.di.CoreDiComponent
import com.coffeeshop.di.multibindings.MultiBindingFactory
import com.coffeeshop.di.multibindings.ViewModelKey
import com.coffeshop.navigation.Route
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Scope

@Scope
internal annotation class FeatureActiveOrdersScope

@Component(
    modules = [
        FeatureActiveOrdersModule::class,
        FeatureActiveOrdersBindingModule::class,
    ],
    dependencies = [CoreDiComponent::class]
)
@FeatureActiveOrdersScope
interface FeatureActiveOrdersComponent {

    fun activeOrdersViewModelFactory(): ViewModelProvider.Factory

    @Component.Builder
    interface Builder {
        @BindsInstance fun router(router: Router<Route>): Builder
        @BindsInstance fun retrofit(retrofit: Retrofit): Builder
        fun coreDiComponent(coreDiComponent: CoreDiComponent): Builder
        fun build(): FeatureActiveOrdersComponent
    }
}

@Module
internal object FeatureActiveOrdersModule {

    @[Provides FeatureActiveOrdersScope]
    fun provideActiveOrdersService(retrofit: Retrofit): ActiveOrdersService = retrofit.create()
}

@Module
internal interface FeatureActiveOrdersBindingModule {

    @Binds
    fun bindActiveOrdersRepository(impl: ActiveOrdersRepositoryImpl): ActiveOrdersRepository

    @Binds
    fun bindGetActiveOrdersUseCase(impl: GetActiveOrdersUseCaseImpl): GetActiveOrdersUseCase

    @Binds
    fun bindViewModelFactory(f: MultiBindingFactory): ViewModelProvider.Factory

    @[Binds IntoMap ViewModelKey(ActiveOrdersViewModel::class)]
    fun bindViewModelIntoMap(vm: ActiveOrdersViewModel): ViewModel
}
