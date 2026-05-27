package com.coffeshop.catalog.internal.di

import androidx.lifecycle.ViewModelProvider
import com.arttttt.nav3router.Router
import com.coffeeshop.buildconfig.api.BuildConfigProvider
import com.coffeeshop.cache.api.Cache
import com.coffeeshop.auth.api.domain.usecase.IsUserLoggedInUseCase
import com.coffeeshop.cart.api.domain.usecase.GetTotalPriceFromCartUseCase
import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.database.di.DatabaseComponent
import com.coffeeshop.di.CoreDiComponent
import com.coffeeshop.di.qualifiers.InMemoryCache
import com.coffeeshop.json.JsonComponent
import com.coffeeshop.logger.api.CoffeeshopLogger
import com.coffeeshop.network.di.NetworkComponent
import com.coffeshop.catalog.api.domain.usecase.GetProductDetailFromCacheUseCase
import com.coffeshop.catalog.api.domain.usecase.RemoveProductDetailFromCacheUseCase
import com.coffeshop.navigation.Route
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        FeatureCatalogModule::class,
        FeatureCatalogBindingModule::class
    ],
    dependencies = [
        NetworkComponent::class,
        DatabaseComponent::class,
        JsonComponent::class,
        CoreDiComponent::class
    ]
)
@FeatureCatalogScope
interface FeatureCatalogComponent {

    val viewModelFactory: ViewModelProvider.Factory

    val getProductDetailFromCacheUseCase: GetProductDetailFromCacheUseCase

    val removeProductDetailFromCacheUseCase: RemoveProductDetailFromCacheUseCase

    @Component.Builder
    interface Builder {
        @BindsInstance fun isUserLoggedIn(isUserLoggedIn: IsUserLoggedInUseCase): Builder
        @BindsInstance fun getTotalPriceFromCart(getTotalPriceFromCartUseCase: GetTotalPriceFromCartUseCase): Builder
        fun jsonComponent(jsonComponent: JsonComponent): Builder
        fun networkComponent(networkComponent: NetworkComponent): Builder
        fun databaseComponent(databaseComponent: DatabaseComponent): Builder
        fun coreDiComponent(coreDiComponent: CoreDiComponent): Builder
        @BindsInstance fun buildConfigProvider(buildConfigProvider: BuildConfigProvider): Builder
        @BindsInstance fun logger(logger: CoffeeshopLogger): Builder
        @BindsInstance fun router(router: Router<Route>): Builder
        @BindsInstance fun productDetailInMemoryCache(@InMemoryCache productDetailCache: Cache<ID, ProductWithModifiers>): Builder
        fun build(): FeatureCatalogComponent
    }
}