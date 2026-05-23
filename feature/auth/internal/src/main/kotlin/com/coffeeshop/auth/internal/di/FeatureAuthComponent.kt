package com.coffeeshop.auth.internal.di

import androidx.lifecycle.ViewModelProvider
import com.arttttt.nav3router.Router
import com.coffeeshop.auth.api.domain.usecase.IsUserLoggedInUseCase
import com.coffeeshop.di.qualifiers.DispatcherIO
import com.coffeeshop.di.qualifiers.LoginViewModelFactory
import com.coffeeshop.di.qualifiers.RegisterViewModelFactory
import com.coffeeshop.network.TokenRepository
import com.coffeshop.navigation.Route
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Retrofit

@AuthScope
@Component(
    modules = [
        FeatureAuthModule::class,
        FeatureAuthBindingModule::class
    ],
)
interface FeatureAuthComponent {

    val isUserLoggedInUseCase: IsUserLoggedInUseCase

    @RegisterViewModelFactory
    fun registerViewModelFactory(): ViewModelProvider.Factory

    @LoginViewModelFactory
    fun loginViewModelFactory(): ViewModelProvider.Factory

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun retrofit(retrofit: Retrofit): Builder
        @BindsInstance
        fun dispatcherIo(@DispatcherIO dispatcher: CoroutineDispatcher): Builder
        @BindsInstance
        fun router(router: Router<Route>): Builder
        @BindsInstance
        fun tokenRepository(tokenRepository: TokenRepository): Builder
        fun build(): FeatureAuthComponent
    }
}