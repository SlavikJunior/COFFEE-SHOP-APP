package com.coffeshop.products.internal.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coffeeshop.di.multibindings.MultiBindingFactory
import com.coffeeshop.di.multibindings.ViewModelKey
import com.coffeshop.products.api.domain.repository.ProductsRepository
import com.coffeshop.products.api.domain.usecase.GetAllModifiersUseCase
import com.coffeshop.products.api.domain.usecase.GetFullMenuUseCase
import com.coffeshop.products.api.domain.usecase.GetMenuByCategoryTypeUseCase
import com.coffeshop.products.api.domain.usecase.GetProductDetailByProductIdUseCase
import com.coffeshop.products.internal.data.repository.CatalogRepositoryImpl
import com.coffeshop.products.internal.domain.usecase.GetAllModifiersUseCaseImpl
import com.coffeshop.products.internal.domain.usecase.GetFullMenuUseCaseImpl
import com.coffeshop.products.internal.domain.usecase.GetMenuByCategoryTypeUseCaseImpl
import com.coffeshop.products.internal.domain.usecase.GetProductDetailByProductIdUseCaseImpl
import com.coffeshop.products.internal.screen.catalog.MyCatalogViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface FeatureCatalogBindingModule {

    @Binds
    fun bindProductsRepository(impl: CatalogRepositoryImpl): ProductsRepository

    @Binds
    fun bindGetFullMenuUseCase(impl: GetFullMenuUseCaseImpl): GetFullMenuUseCase

    @Binds
    fun bindGetMenuByCategoryTypeUseCase(impl: GetMenuByCategoryTypeUseCaseImpl): GetMenuByCategoryTypeUseCase

    @Binds
    fun bindGetProductDetailUseCase(impl: GetProductDetailByProductIdUseCaseImpl): GetProductDetailByProductIdUseCase

    @Binds
    fun bindGetAllModifiersUseCase(impl: GetAllModifiersUseCaseImpl): GetAllModifiersUseCase

    @Binds
    @IntoMap
    @ViewModelKey(MyCatalogViewModel::class)
    fun provideMyCatalogViewModel(viewModel: MyCatalogViewModel): ViewModel

    @Binds
    fun bindViewModelFactory(f: MultiBindingFactory): ViewModelProvider.Factory
}
