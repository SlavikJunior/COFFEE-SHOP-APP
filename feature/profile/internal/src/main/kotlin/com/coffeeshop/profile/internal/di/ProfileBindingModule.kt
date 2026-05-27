package com.coffeeshop.profile.internal.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coffeeshop.di.multibindings.MultiBindingFactory
import com.coffeeshop.di.multibindings.ViewModelKey
import com.coffeeshop.profile.api.domain.repository.ProfileRepository
import com.coffeeshop.profile.api.domain.usecase.GetProfileUseCase
import com.coffeeshop.profile.api.domain.usecase.LogoutUseCase
import com.coffeeshop.profile.internal.data.repository.ProfileRepositoryImpl
import com.coffeeshop.profile.internal.domain.usecase.GetProfileUseCaseImpl
import com.coffeeshop.profile.internal.domain.usecase.LogoutUseCaseImpl
import com.coffeeshop.profile.internal.screen.ProfileViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface ProfileBindingModule {

    @Binds
    fun bindProfileRepository(target: ProfileRepositoryImpl): ProfileRepository

    @Binds
    fun bindViewModelFactory(f: MultiBindingFactory): ViewModelProvider.Factory

    @[Binds IntoMap ViewModelKey(ProfileViewModel::class)]
    fun provideProfileViewModel(vm: ProfileViewModel): ViewModel

    @Binds
    fun bindGetProfileUseCase(impl: GetProfileUseCaseImpl): GetProfileUseCase

    @Binds
    fun bindLogoutUseCase(impl: LogoutUseCaseImpl): LogoutUseCase
}
