package com.coffeeshop.network.di

import android.content.Context
import com.coffeeshop.buildconfig.api.BuildConfigProvider
import com.coffeeshop.di.CoreDiComponent
import com.coffeeshop.di.qualifiers.ApplicationContext
import com.coffeeshop.json.JsonComponent
import dagger.BindsInstance
import dagger.Component
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Component(
    modules = [NetworkModule::class],
    dependencies = [
        CoreDiComponent::class,
        JsonComponent::class
    ]
)
@NetworkScope
interface NetworkComponent {

    val retrofit: Retrofit

    val okHttpClient: OkHttpClient

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun applicationContext(@ApplicationContext context: Context): Builder

        fun coreDiComponent(coreDiComponent: CoreDiComponent): Builder

        @BindsInstance
        fun buildConfigProvider(buildConfigProvider: BuildConfigProvider): Builder

        fun jsonComponent(jsonComponent: JsonComponent): Builder

        fun build(): NetworkComponent
    }
}