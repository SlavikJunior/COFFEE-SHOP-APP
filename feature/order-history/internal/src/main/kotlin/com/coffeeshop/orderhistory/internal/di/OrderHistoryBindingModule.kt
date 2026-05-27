package com.coffeeshop.orderhistory.internal.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coffeeshop.di.multibindings.MultiBindingFactory
import com.coffeeshop.di.multibindings.ViewModelKey
import com.coffeeshop.orderhistory.api.domain.repository.OrderHistoryRepository
import com.coffeeshop.orderhistory.api.domain.usecase.GetOrderHistoryUseCase
import com.coffeeshop.orderhistory.internal.data.repository.OrderHistoryRepositoryImpl
import com.coffeeshop.orderhistory.internal.domain.usecase.GetOrderHistoryUseCaseImpl
import com.coffeeshop.orderhistory.internal.screen.OrderHistoryViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface OrderHistoryBindingModule {

    @Binds
    fun bindOrderHistoryRepository(impl: OrderHistoryRepositoryImpl): OrderHistoryRepository

    @Binds
    fun bindGetOrderHistoryUseCase(impl: GetOrderHistoryUseCaseImpl): GetOrderHistoryUseCase

    @Binds
    fun bindViewModelFactory(f: MultiBindingFactory): ViewModelProvider.Factory

    @[Binds IntoMap ViewModelKey(OrderHistoryViewModel::class)]
    fun provideOrderHistoryViewModel(vm: OrderHistoryViewModel): ViewModel
}