package com.coffeeshop.auth.internal.di

import com.coffeeshop.auth.api.domain.repository.AuthRepository
import com.coffeeshop.auth.api.domain.usecase.RegisterByPhoneNumberAndNameAndSmsCodeUseCase
import com.coffeeshop.auth.api.domain.usecase.SendSmsCodeByPhoneNumberUseCase
import com.coffeeshop.auth.api.domain.usecase.VerifySmsCodeByPhoneNumberUseCase
import com.coffeeshop.auth.internal.data.repository.AuthRepositoryImpl
import com.coffeeshop.auth.internal.domain.usecase.RegisterByPhoneNumberAndNameAndSmsCodeUseCaseImpl
import com.coffeeshop.auth.internal.domain.usecase.SendSmsCodeByPhoneNumberUseCaseImpl
import com.coffeeshop.auth.internal.domain.usecase.VerifySmsCodeByPhoneNumberUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
internal interface FeatureAuthBindingModule {

    @Binds
    @IntoSet
    fun bindRegisterUseCase(target: RegisterByPhoneNumberAndNameAndSmsCodeUseCaseImpl): RegisterByPhoneNumberAndNameAndSmsCodeUseCase

    @Binds
    @IntoSet
    fun bindSendSmsCodeUseCase(target: SendSmsCodeByPhoneNumberUseCaseImpl): SendSmsCodeByPhoneNumberUseCase

    @Binds
    @IntoSet
    fun bindVerifySmsCodeUseCase(target: VerifySmsCodeByPhoneNumberUseCaseImpl): VerifySmsCodeByPhoneNumberUseCase

    @Binds
    @IntoSet
    fun bindAuthRepository(target: AuthRepositoryImpl): AuthRepository
}