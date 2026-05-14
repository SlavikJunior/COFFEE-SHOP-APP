package com.coffeeshop.profile.internal.data.service

import com.coffeeshop.profile.internal.di.ProfileScope
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.create

@Module
object ProfileModule {

    @[Provides ProfileScope]
    fun provideProfileService(
        retrofit: Retrofit
    ): ProfileService = retrofit.create()
}
