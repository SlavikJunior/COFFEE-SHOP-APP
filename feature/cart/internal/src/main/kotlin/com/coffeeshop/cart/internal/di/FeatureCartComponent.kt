package com.coffeeshop.cart.internal.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arttttt.nav3router.Router
import com.coffeeshop.cart.api.domain.repository.CartRepository
import com.coffeeshop.cart.api.domain.usecase.AddToCartUseCase
import com.coffeeshop.cart.api.domain.usecase.GetCartItemsUseCase
import com.coffeeshop.cart.api.domain.usecase.GetTotalPriceFromCartUseCase
import com.coffeeshop.cart.api.domain.usecase.LoadCartDataUseCase
import com.coffeeshop.cart.api.domain.usecase.RemoveFromCartUseCase
import com.coffeeshop.cart.api.domain.usecase.SaveCartStateUseCase
import com.coffeeshop.cart.internal.data.repository.CartRepositoryImpl
import com.coffeeshop.cart.internal.data.service.CartService
import com.coffeeshop.cart.internal.domain.usecase.AddToCartUseCaseImpl
import com.coffeeshop.cart.internal.domain.usecase.GetCartItemsUseCaseImpl
import com.coffeeshop.cart.internal.domain.usecase.GetTotalPriceFromCartUseCaseImpl
import com.coffeeshop.cart.internal.domain.usecase.LoadCartDataUseCaseImpl
import com.coffeeshop.cart.internal.domain.usecase.RemoveFromCartUseCaseImpl
import com.coffeeshop.cart.internal.domain.usecase.SaveCartStateUseCaseImpl
import com.coffeeshop.cart.internal.screen.cart.CartViewModel
import com.coffeeshop.database.di.DatabaseComponent
import com.coffeeshop.di.CoreDiComponent
import com.coffeeshop.di.multibindings.MultiBindingFactory
import com.coffeeshop.di.multibindings.ViewModelKey
import com.coffeeshop.json.JsonComponent
import com.coffeeshop.logger.api.CoffeeshopLogger
import com.coffeeshop.network.di.NetworkComponent
import com.coffeshop.navigation.Route
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import retrofit2.Retrofit
import javax.inject.Scope

@Scope
internal annotation class FeatureCartComponentScope

@Component(
    modules = [
        FeatureCartModule::class,
        FeatureCartBindingModule::class,
    ],
    dependencies = [
        NetworkComponent::class,
        DatabaseComponent::class,
        CoreDiComponent::class,
        JsonComponent::class,
    ]
)
@FeatureCartComponentScope
interface FeatureCartComponent {

    val viewModelFactory: ViewModelProvider.Factory

    val getTotalPriceFromCartUseCase: GetTotalPriceFromCartUseCase

    val addToCartUseCase: AddToCartUseCase

    @Component.Builder
    interface Builder {
        @BindsInstance fun logger(logger: CoffeeshopLogger): Builder
        @BindsInstance fun router(router: Router<Route>): Builder
        fun networkComponent(networkComponent: NetworkComponent): Builder
        fun databaseComponent(databaseComponent: DatabaseComponent): Builder
        fun coreDiComponent(coreDiComponent: CoreDiComponent): Builder
        fun jsonComponent(jsonComponent: JsonComponent): Builder
        fun build(): FeatureCartComponent
    }
}

@Module
internal object FeatureCartModule {

    @Provides
    @FeatureCartComponentScope
    fun provideCartService(retrofit: Retrofit): CartService = retrofit.create(CartService::class.java)

}

@Module
internal interface FeatureCartBindingModule {

    @Binds
    @FeatureCartComponentScope
    fun bindCartRepositoryToImpl(impl: CartRepositoryImpl): CartRepository

    @Binds
    fun bindGetTotalPriceFromCartUseCaseToImpl(impl: GetTotalPriceFromCartUseCaseImpl): GetTotalPriceFromCartUseCase

    @Binds
    fun bindRemoveFromCartUseCaseToImpl(impl: RemoveFromCartUseCaseImpl): RemoveFromCartUseCase

    @Binds
    fun bindAddToCartUseCaseToImpl(impl: AddToCartUseCaseImpl): AddToCartUseCase

    @Binds
    fun bindGetCartItemsUseCaseToImpl(impl: GetCartItemsUseCaseImpl): GetCartItemsUseCase

    @Binds
    fun bindLoadCartDataUseCaseToImpl(impl: LoadCartDataUseCaseImpl): LoadCartDataUseCase

    @Binds
    fun bindSaveCartStateUseCaseToImpl(impl: SaveCartStateUseCaseImpl): SaveCartStateUseCase

    @Binds
    fun bindViewModelFactory(f: MultiBindingFactory): ViewModelProvider.Factory

    @[Binds IntoMap ViewModelKey(CartViewModel::class)]
    fun bindViewModelIntoMap(vm: CartViewModel): ViewModel
}
