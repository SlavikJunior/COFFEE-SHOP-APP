package com.coffeeshop.profile.internal.di

import androidx.lifecycle.ViewModelProvider
import com.arttttt.nav3router.Router
import com.coffeeshop.auth.api.domain.usecase.IsUserLoggedInUseCase
import com.coffeeshop.di.CoreDiComponent
import com.coffeeshop.network.NotificationsRepository
import com.coffeeshop.network.TokenRepository
import com.coffeeshop.profile.internal.data.service.ProfileModule
import com.coffeshop.navigation.Route
import dagger.BindsInstance
import dagger.Component
import retrofit2.Retrofit

@Component(
    modules = [
        ProfileModule::class,
        ProfileBindingModule::class,
    ],
    dependencies = [CoreDiComponent::class],
)
@ProfileScope
interface FeatureProfileComponent {

    val viewModelFactory: ViewModelProvider.Factory

    @Component.Builder
    interface Builder {
        @BindsInstance fun isUserLoggedIn(isUserLoggedIn: IsUserLoggedInUseCase): Builder
        fun coreDiComponent(coreDiComponent: CoreDiComponent): Builder

        @BindsInstance fun retrofit(retrofit: Retrofit): Builder

        @BindsInstance fun router(router: Router<Route>): Builder

        @BindsInstance fun tokenRepository(tokenRepository: TokenRepository): Builder

        @BindsInstance fun notificationsRepository(repo: NotificationsRepository): Builder

        fun build(): FeatureProfileComponent
    }
}
