package com.coffeshop.products.internal.di

import com.coffeshop.products.api.domain.repository.ProductsRepository
import com.coffeshop.products.api.domain.usecase.GetAllModifiersUseCase
import com.coffeshop.products.api.domain.usecase.GetFullMenuUseCase
import com.coffeshop.products.api.domain.usecase.GetMenuByCategoryTypeUseCase
import com.coffeshop.products.api.domain.usecase.GetProductDetailByProductIdUseCase
import com.coffeshop.products.internal.data.repository.ProductsRepositoryImpl
import com.coffeshop.products.internal.domain.usecase.GetAllModifiersUseCaseImpl
import com.coffeshop.products.internal.domain.usecase.GetFullMenuUseCaseImpl
import com.coffeshop.products.internal.domain.usecase.GetMenuByCategoryTypeUseCaseImpl
import com.coffeshop.products.internal.domain.usecase.GetProductDetailByProductIdUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
internal interface FeatureProductsBindingModule {

    @Binds
    fun bindProductsRepository(impl: ProductsRepositoryImpl): ProductsRepository

    @Binds
    fun bindGetFullMenuUseCase(impl: GetFullMenuUseCaseImpl): GetFullMenuUseCase

    @Binds
    fun bindGetMenuByCategoryTypeUseCase(impl: GetMenuByCategoryTypeUseCaseImpl): GetMenuByCategoryTypeUseCase

    @Binds
    fun bindGetProductDetailUseCase(impl: GetProductDetailByProductIdUseCaseImpl): GetProductDetailByProductIdUseCase

    @Binds
    fun bindGetAllModifiersUseCase(impl: GetAllModifiersUseCaseImpl): GetAllModifiersUseCase
}
