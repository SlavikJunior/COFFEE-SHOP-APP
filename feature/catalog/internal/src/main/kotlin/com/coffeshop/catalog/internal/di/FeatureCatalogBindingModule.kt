package com.coffeshop.catalog.internal.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coffeeshop.di.multibindings.MultiBindingFactory
import com.coffeeshop.di.multibindings.ViewModelKey
import com.coffeshop.catalog.api.domain.repository.CatalogRepository
import com.coffeshop.catalog.api.domain.usecase.GetAllModifiersUseCase
import com.coffeshop.catalog.api.domain.usecase.GetFullMenuUseCase
import com.coffeshop.catalog.api.domain.usecase.GetMenuByCategoryTypeUseCase
import com.coffeshop.catalog.api.domain.usecase.GetProductDetailByProductIdUseCase
import com.coffeshop.catalog.internal.data.repository.CatalogRepositoryImpl
import com.coffeshop.catalog.internal.domain.usecase.GetAllModifiersUseCaseImpl
import com.coffeshop.catalog.internal.domain.usecase.GetFullMenuUseCaseImpl
import com.coffeshop.catalog.internal.domain.usecase.GetMenuByCategoryTypeUseCaseImpl
import com.coffeshop.catalog.internal.domain.usecase.GetProductDetailByProductIdUseCaseImpl
import com.coffeshop.catalog.internal.screen.catalog.CatalogViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface FeatureCatalogBindingModule {

    @Binds
    fun bindProductsRepository(impl: CatalogRepositoryImpl): CatalogRepository

    @Binds
    fun bindGetFullMenuUseCase(impl: GetFullMenuUseCaseImpl): GetFullMenuUseCase

    @Binds
    fun bindGetMenuByCategoryTypeUseCase(impl: GetMenuByCategoryTypeUseCaseImpl): GetMenuByCategoryTypeUseCase

    @Binds
    fun bindGetProductDetailUseCase(impl: GetProductDetailByProductIdUseCaseImpl): GetProductDetailByProductIdUseCase

    @Binds
    fun bindGetAllModifiersUseCase(impl: GetAllModifiersUseCaseImpl): GetAllModifiersUseCase

    @[Binds IntoMap ViewModelKey(CatalogViewModel::class)]
    fun provideMyCatalogViewModel(viewModel: CatalogViewModel): ViewModel

    @Binds
    fun bindViewModelFactory(f: MultiBindingFactory): ViewModelProvider.Factory
}
