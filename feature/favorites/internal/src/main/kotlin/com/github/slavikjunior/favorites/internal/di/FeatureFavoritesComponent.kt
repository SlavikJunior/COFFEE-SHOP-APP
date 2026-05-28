package com.github.slavikjunior.favorites.internal.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arttttt.nav3router.Router
import com.coffeeshop.auth.api.domain.usecase.IsUserLoggedInUseCase
import com.coffeeshop.buildconfig.api.BuildConfigProvider
import com.coffeeshop.cache.api.Cache
import com.coffeeshop.cart.api.domain.usecase.GetTotalPriceFromCartUseCase
import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.database.di.DatabaseComponent
import com.coffeeshop.di.CoreDiComponent
import com.coffeeshop.di.multibindings.MultiBindingFactory
import com.coffeeshop.di.multibindings.ViewModelKey
import com.coffeeshop.di.qualifiers.InMemoryCache
import com.coffeeshop.logger.api.CoffeeshopLogger
import com.coffeshop.catalog.api.domain.usecase.GetProductDetailByProductIdUseCase
import com.coffeshop.catalog.api.domain.usecase.IsProductDetailStoredInCacheUseCase
import com.coffeshop.catalog.api.domain.usecase.SaveProductDetailInCacheUseCase
import com.coffeshop.navigation.Route
import com.github.slavikjunior.favorites.api.domain.repository.FavoritesRepository
import com.github.slavikjunior.favorites.api.domain.usecase.GetAllFavoritesProductsUseCase
import com.github.slavikjunior.favorites.api.domain.usecase.ToggleProductByIdUseCase
import com.github.slavikjunior.favorites.internal.data.repository.FavoritesRepositoryImpl
import com.github.slavikjunior.favorites.internal.domain.usecase.GetAllFavoritesProductsUseCaseImpl
import com.github.slavikjunior.favorites.internal.domain.usecase.ToggleProductByIdUseCaseImpl
import com.github.slavikjunior.favorites.internal.screen.favorites.FavoritesViewModel
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Scope

@Scope
internal annotation class FeatureFavoritesScope

@Component(
    modules = [
        FeatureFavoritesModule::class,
        FeatureFavoritesBindingModule::class
    ],
    dependencies = [
        DatabaseComponent::class,
        CoreDiComponent::class
    ]
)
@FeatureFavoritesScope
interface FeatureFavoritesComponent {

    val viewModelFactory: ViewModelProvider.Factory

    @Component.Builder
    interface Builder {
        fun databaseComponent(databaseComponent: DatabaseComponent): Builder
        fun coreDiComponent(coreDiComponent: CoreDiComponent): Builder
        @BindsInstance fun isUserLoggedIn(isUserLoggedIn: IsUserLoggedInUseCase): Builder
        @BindsInstance fun getTotalPriceFromCart(getTotalPriceFromCartUseCase: GetTotalPriceFromCartUseCase): Builder
        @BindsInstance fun buildConfigProvider(buildConfigProvider: BuildConfigProvider): Builder
        @BindsInstance fun logger(logger: CoffeeshopLogger): Builder
        @BindsInstance fun router(router: Router<Route>): Builder
        @BindsInstance fun productDetailInMemoryCache(@InMemoryCache productDetailCache: Cache<ID, ProductWithModifiers>): Builder
        @BindsInstance fun getProductDetailByProductId(uc: GetProductDetailByProductIdUseCase): Builder
        @BindsInstance fun isProductDetailStoredInCache(uc: IsProductDetailStoredInCacheUseCase): Builder
        @BindsInstance fun saveProductDetailInCache(uc: SaveProductDetailInCacheUseCase): Builder
        fun build(): FeatureFavoritesComponent
    }
}

@Module
internal object FeatureFavoritesModule {}

@Module
internal interface FeatureFavoritesBindingModule {
    @Binds fun bindFavoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository
    @Binds fun bindGetAllFavorites(impl: GetAllFavoritesProductsUseCaseImpl): GetAllFavoritesProductsUseCase
    @Binds fun bindToggleProduct(impl: ToggleProductByIdUseCaseImpl): ToggleProductByIdUseCase
    @[Binds IntoMap ViewModelKey(FavoritesViewModel::class)]
    fun bindFavoritesViewModel(vm: FavoritesViewModel): ViewModel
    @Binds fun bindViewModelFactory(f: MultiBindingFactory): ViewModelProvider.Factory
}