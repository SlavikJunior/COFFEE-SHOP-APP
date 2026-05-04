package com.coffeshop.catalog.internal.di

import androidx.lifecycle.ViewModelProvider
import com.arttttt.nav3router.Router
import com.coffeeshop.database.di.DatabaseComponent
import com.coffeeshop.di.CoreDiComponent
import com.coffeeshop.di.qualifiers.DispatcherIO
import com.coffeeshop.json.JsonComponent
import com.coffeeshop.network.di.NetworkComponent
import com.coffeshop.navigation.Route
import com.coffeshop.navigation.di.CoreNavigationComponent
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Retrofit

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

    @Component.Builder
    interface Builder {
        fun jsonComponent(jsonComponent: JsonComponent): Builder
        fun networkComponent(networkComponent: NetworkComponent): Builder
        fun databaseComponent(databaseComponent: DatabaseComponent): Builder
        fun coreDiComponent(coreDiComponent: CoreDiComponent): Builder
        @BindsInstance fun router(router: Router<Route>): Builder
        fun build(): FeatureCatalogComponent
    }
}