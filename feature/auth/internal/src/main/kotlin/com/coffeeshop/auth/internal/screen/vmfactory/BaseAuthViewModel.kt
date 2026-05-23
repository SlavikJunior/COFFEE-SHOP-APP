package com.coffeeshop.auth.internal.screen.vmfactory

import androidx.lifecycle.ViewModel
import com.coffeeshop.utils.validateRussianPhoneNumberBy_E_164

internal abstract class BaseAuthViewModel : ViewModel() {

    fun isNavigateWithPhoneAble(currentPhone: String) = validateRussianPhoneNumberBy_E_164("$RUSSIAN_PHONE_NUMBER_PREFIX$currentPhone")

    private companion object {
        const val RUSSIAN_PHONE_NUMBER_PREFIX = "+7"
    }
}