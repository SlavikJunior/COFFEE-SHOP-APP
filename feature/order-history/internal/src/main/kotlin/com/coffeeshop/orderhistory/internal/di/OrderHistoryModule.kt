package com.coffeeshop.orderhistory.internal.di

import com.coffeeshop.orderhistory.internal.data.service.OrderHistoryService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.create

@Module
internal object OrderHistoryModule {

    @Provides
    @OrderHistoryScope
    fun provideOrderHistoryService(retrofit: Retrofit): OrderHistoryService = retrofit.create()
}