package com.coffeeshop.product_detail.internal.di

import androidx.lifecycle.ViewModelProvider
import com.arttttt.nav3router.Router
import com.coffeeshop.cache.api.Cache
import com.coffeeshop.cart.api.domain.usecase.AddToCartUseCase
import com.coffeeshop.common.model.products.ProductWithModifiers
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.di.CoreDiComponent
import com.coffeeshop.di.qualifiers.InMemoryCache
import com.coffeeshop.logger.api.CoffeeshopLogger
import com.coffeshop.catalog.api.domain.usecase.GetProductDetailFromCacheUseCase
import com.coffeshop.catalog.api.domain.usecase.RemoveProductDetailFromCacheUseCase
import com.coffeshop.navigation.Route
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [FeatureProductDetailBindingModule::class],
    dependencies = [
        CoreDiComponent::class
    ]
)
@ProductDetailScope
interface FeatureProductDetailComponent {

    val viewModelFactory: ViewModelProvider.Factory

    @Component.Builder
    interface Builder {

        fun coreDiComponent(coreDiComponent: CoreDiComponent): Builder

        @BindsInstance fun logger(logger: CoffeeshopLogger): Builder

        @BindsInstance fun router(router: Router<Route>): Builder

        @BindsInstance fun productDetailInMemoryCache(@InMemoryCache productDetailCache: Cache<ID, ProductWithModifiers>): Builder

        @BindsInstance fun getProductDetailFromCacheUseCase(useCase: GetProductDetailFromCacheUseCase): Builder

        @BindsInstance fun removeProductDetailFromCacheUseCase(useCase: RemoveProductDetailFromCacheUseCase): Builder

        @BindsInstance fun addToCartUseCase(useCase: AddToCartUseCase): Builder

        fun build(): FeatureProductDetailComponent
    }
}