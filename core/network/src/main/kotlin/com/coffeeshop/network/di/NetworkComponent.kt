package com.coffeeshop.network.di

import android.content.Context
import com.coffeeshop.buildconfig.api.BuildConfigProvider
import com.coffeeshop.di.qualifiers.ApplicationContext
import dagger.BindsInstance
import dagger.Component
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Component(modules = [NetworkModule::class])
@NetworkScope
internal interface NetworkComponent {

    val retrofit: Retrofit

    val okHttpClient: OkHttpClient

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun applicationContext(@ApplicationContext context: Context): Builder

        @BindsInstance
        fun buildConfigProvider(buildConfigProvider: BuildConfigProvider): Builder

        fun build(): NetworkComponent
    }
}