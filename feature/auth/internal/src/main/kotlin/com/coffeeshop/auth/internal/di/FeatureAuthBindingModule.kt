package com.coffeeshop.auth.internal.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coffeeshop.auth.api.domain.repository.AuthRepository
import com.coffeeshop.auth.api.domain.usecase.IsUserLoggedInUseCase
import com.coffeeshop.auth.api.domain.usecase.RegisterByFirebaseIdTokenAndNameUseCase
import com.coffeeshop.auth.api.domain.usecase.VerifyFirebaseTokenUseCase
import com.coffeeshop.auth.internal.data.repository.AuthRepositoryImpl
import com.coffeeshop.auth.internal.domain.usecase.IsUserLoggedInUseCaseImpl
import com.coffeeshop.auth.internal.domain.usecase.RegisterByFirebaseIdTokenAndNameUseCaseImpl
import com.coffeeshop.auth.internal.domain.usecase.VerifyFirebaseTokenUseCaseImpl
import com.coffeeshop.auth.internal.screen.login.LoginViewModel
import com.coffeeshop.auth.internal.screen.register.RegisterViewModel
import com.coffeeshop.di.multibindings.MultiBindingFactory
import com.coffeeshop.di.multibindings.ViewModelKey
import com.coffeeshop.di.qualifiers.LoginViewModelFactory
import com.coffeeshop.di.qualifiers.RegisterViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface FeatureAuthBindingModule {

    @Binds
    fun bindIsUserLoggedInUseCase(target: IsUserLoggedInUseCaseImpl): IsUserLoggedInUseCase

    @Binds
    fun bindRegisterUseCase(target: RegisterByFirebaseIdTokenAndNameUseCaseImpl): RegisterByFirebaseIdTokenAndNameUseCase

    @Binds
    fun bindVerifyFirebaseTokenUseCase(target: VerifyFirebaseTokenUseCaseImpl): VerifyFirebaseTokenUseCase

    @[Binds AuthScope]
    fun bindAuthRepository(target: AuthRepositoryImpl): AuthRepository

    @[Binds IntoMap ViewModelKey(RegisterViewModel::class)]
    fun bindRegisterViewModel(vm: RegisterViewModel): ViewModel

    @[Binds IntoMap ViewModelKey(LoginViewModel::class)]
    fun bindLoginViewModel(vm: LoginViewModel): ViewModel

    @[Binds RegisterViewModelFactory]
    fun bindRegisterViewModelFactory(f: MultiBindingFactory): ViewModelProvider.Factory

    @[Binds LoginViewModelFactory]
    fun bindLoginViewModelFactory(f: MultiBindingFactory): ViewModelProvider.Factory
}