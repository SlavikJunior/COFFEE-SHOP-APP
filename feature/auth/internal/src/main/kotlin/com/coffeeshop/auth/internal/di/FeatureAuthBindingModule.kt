package com.coffeeshop.auth.internal.di

import androidx.lifecycle.ViewModel
import com.coffeeshop.auth.api.domain.repository.AuthRepository
import com.coffeeshop.auth.api.domain.usecase.RegisterByPhoneNumberAndNameAndSmsCodeUseCase
import com.coffeeshop.auth.api.domain.usecase.SendSmsCodeByPhoneNumberUseCase
import com.coffeeshop.auth.api.domain.usecase.VerifySmsCodeByPhoneNumberUseCase
import com.coffeeshop.auth.internal.data.repository.AuthRepositoryImpl
import com.coffeeshop.auth.internal.domain.usecase.RegisterByPhoneNumberAndNameAndSmsCodeUseCaseImpl
import com.coffeeshop.auth.internal.domain.usecase.SendSmsCodeByPhoneNumberUseCaseImpl
import com.coffeeshop.auth.internal.domain.usecase.VerifySmsCodeByPhoneNumberUseCaseImpl
import com.coffeeshop.auth.internal.screen.login.LoginViewModel
import com.coffeeshop.auth.internal.screen.register.RegisterViewModel
import com.coffeeshop.auth.internal.screen.vmfactory.SavedStateHandleFactory
import com.coffeeshop.di.bindingkey.ViewModelKey
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet
import kotlin.reflect.KClass

@Module
internal interface FeatureAuthBindingModule {

    @Binds
    fun bindRegisterUseCase(target: RegisterByPhoneNumberAndNameAndSmsCodeUseCaseImpl): RegisterByPhoneNumberAndNameAndSmsCodeUseCase

    @Binds
    fun bindSendSmsCodeUseCase(target: SendSmsCodeByPhoneNumberUseCaseImpl): SendSmsCodeByPhoneNumberUseCase

    @Binds
    fun bindVerifySmsCodeUseCase(target: VerifySmsCodeByPhoneNumberUseCaseImpl): VerifySmsCodeByPhoneNumberUseCase

    @Binds
    fun bindAuthRepository(target: AuthRepositoryImpl): AuthRepository

    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel::class)
    fun bindRegisterViewModel(factory: RegisterViewModel.Factory): SavedStateHandleFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    fun bindLoginViewModel(factory: LoginViewModel.Factory): SavedStateHandleFactory<out ViewModel>
}