package com.coffeeshop.product_detail.internal.di

import androidx.lifecycle.ViewModelProvider
import com.arttttt.nav3router.Router
import com.coffeeshop.di.CoreDiComponent
import com.coffeshop.navigation.Route
import com.coffeshop.navigation.di.CoreNavigationComponent
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

        @BindsInstance fun router(router: Router<Route>): Builder

        fun build(): FeatureProductDetailComponent
    }
}