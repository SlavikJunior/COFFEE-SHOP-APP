package com.coffeeshop.product_detail.internal.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coffeeshop.di.multibindings.MultiBindingFactory
import com.coffeeshop.di.multibindings.ViewModelKey
import com.coffeeshop.product_detail.api.domain.usecase.CalculateProductTotalPriceUseCase
import com.coffeeshop.product_detail.api.domain.repository.ProductDetailRepository
import com.coffeeshop.product_detail.api.domain.usecase.DecrementQuantityUseCase
import com.coffeeshop.product_detail.api.domain.usecase.IncrementQuantityUseCase
import com.coffeeshop.product_detail.internal.data.repositpry.ProductDetailRepositoryImpl
import com.coffeeshop.product_detail.internal.domain.usecase.CalculateProductTotalPriceUseCaseImpl
import com.coffeeshop.product_detail.internal.domain.usecase.DecrementQuantityUseCaseImpl
import com.coffeeshop.product_detail.internal.domain.usecase.IncrementQuantityUseCaseImpl
import com.coffeeshop.product_detail.internal.screen.product_detail.ProductDetailViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface FeatureProductDetailBindingModule {

    @Binds
    fun bindIncrementQuantityUseCaseToImpl(impl: IncrementQuantityUseCaseImpl): IncrementQuantityUseCase

    @Binds
    fun bindDecrementQuantityUseCaseToImpl(impl: DecrementQuantityUseCaseImpl): DecrementQuantityUseCase

    @Binds
    fun bindCalculateProductTotalPriceUseCaseToImpl(impl: CalculateProductTotalPriceUseCaseImpl): CalculateProductTotalPriceUseCase

    @Binds
    fun bindProductDetailRepositoryToImpl(impl: ProductDetailRepositoryImpl): ProductDetailRepository

    @Binds
    fun bindViewModelFactory(f: MultiBindingFactory): ViewModelProvider.Factory

    @[Binds IntoMap ViewModelKey(ProductDetailViewModel::class)]
    fun provideProductDetailViewModel(vm: ProductDetailViewModel): ViewModel
}