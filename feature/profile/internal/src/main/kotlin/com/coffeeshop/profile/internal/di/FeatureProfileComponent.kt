package com.coffeeshop.profile.internal.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.arttttt.nav3router.Router
import com.coffeeshop.di.CoreDiComponent
import com.coffeeshop.di.qualifiers.ApplicationContext
import com.coffeeshop.di.qualifiers.DispatcherIO
import com.coffeeshop.profile.internal.data.service.ProfileModule
import com.coffeshop.navigation.Route
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Retrofit

@Component(
    modules = [
        ProfileModule::class,
        ProfileBindingModule::class
    ],
    dependencies = [CoreDiComponent::class]
)
@ProfileScope
interface FeatureProfileComponent {

    val viewModelFactory: ViewModelProvider.Factory

    @Component.Builder
    interface Builder {
        fun coreDiComponent(coreDiComponent: CoreDiComponent): Builder
        @BindsInstance fun applicationContext(@ApplicationContext applicationContext: Context): Builder
        @BindsInstance fun retrofit(retrofit: Retrofit): Builder
        @BindsInstance fun router(router: Router<Route>): Builder
        fun build(): FeatureProfileComponent
    }
}
