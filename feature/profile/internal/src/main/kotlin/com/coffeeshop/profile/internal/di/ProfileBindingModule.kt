package com.coffeeshop.profile.internal.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coffeeshop.di.multibindings.MultiBindingFactory
import com.coffeeshop.di.multibindings.ViewModelKey
import com.coffeeshop.profile.api.domain.repository.ProfileRepository
import com.coffeeshop.profile.api.domain.usecase.ChangeEmailUseCase
import com.coffeeshop.profile.api.domain.usecase.ChangeNameUseCase
import com.coffeeshop.profile.api.domain.usecase.ChangePhoneNumberUseCase
import com.coffeeshop.profile.api.domain.usecase.GetOrderHistoryUseCase
import com.coffeeshop.profile.api.domain.usecase.GetProfileUseCase
import com.coffeeshop.profile.api.domain.usecase.LogoutUseCase
import com.coffeeshop.profile.api.domain.usecase.SendFeedBackUseCase
import com.coffeeshop.profile.api.domain.usecase.ToggleGetNotificationsUseCase
import com.coffeeshop.profile.internal.data.repository.ProfileRepositoryImpl
import com.coffeeshop.profile.internal.domain.usecase.ChangeEmailUseCaseImpl
import com.coffeeshop.profile.internal.domain.usecase.ChangeNameUseCaseImpl
import com.coffeeshop.profile.internal.domain.usecase.ChangePhoneNumberUseCaseImpl
import com.coffeeshop.profile.internal.domain.usecase.GetOrderHistoryUseCaseImpl
import com.coffeeshop.profile.internal.domain.usecase.GetProfileUseCaseImpl
import com.coffeeshop.profile.internal.domain.usecase.LogoutUseCaseImpl
import com.coffeeshop.profile.internal.domain.usecase.SendFeedBackUseCaseImpl
import com.coffeeshop.profile.internal.domain.usecase.ToggleGetNotificationsUseCaseImpl
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
    fun bindChangeEmailUseCaseToImpl(impl: ChangeEmailUseCaseImpl): ChangeEmailUseCase

    @Binds
    fun bindChangeNameUseCaseToImpl(impl: ChangeNameUseCaseImpl): ChangeNameUseCase

    @Binds
    fun bindChangePhoneNumberUseCaseToImpl(impl: ChangePhoneNumberUseCaseImpl): ChangePhoneNumberUseCase

    @Binds
    fun bindGetOrderHistoryUseCaseToImpl(impl: GetOrderHistoryUseCaseImpl): GetOrderHistoryUseCase

    @Binds
    fun bindGetProfileUseCaseToImpl(impl: GetProfileUseCaseImpl): GetProfileUseCase

    @Binds
    fun bindLogoutUseCaseToImpl(impl: LogoutUseCaseImpl): LogoutUseCase

    @Binds
    fun bindSendFeedBackUseCaseToImpl(impl: SendFeedBackUseCaseImpl): SendFeedBackUseCase

    @Binds
    fun bindToggleGetNotificationsUseCaseToImpl(impl: ToggleGetNotificationsUseCaseImpl): ToggleGetNotificationsUseCase
}