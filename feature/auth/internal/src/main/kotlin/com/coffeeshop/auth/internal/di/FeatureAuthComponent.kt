package com.coffeeshop.auth.internal.di

import com.arttttt.nav3router.Router
import com.coffeeshop.di.qualifiers.DispatcherIO
import com.coffeshop.navigation.Route
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Retrofit

@AuthScope
@Component(
    modules = [FeatureAuthModule::class],
)
interface FeatureAuthComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance fun retrofit(retrofit: Retrofit): Builder
        @BindsInstance fun dispatcherIo(@DispatcherIO dispatcher: CoroutineDispatcher): Builder
        @BindsInstance fun router(router: Router<Route>): Builder
        fun build(): FeatureAuthComponent
    }
}