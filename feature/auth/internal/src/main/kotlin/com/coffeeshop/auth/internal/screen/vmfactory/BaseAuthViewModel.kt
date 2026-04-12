package com.coffeeshop.auth.internal.screen.vmfactory

import androidx.lifecycle.ViewModel
import com.coffeshop.utils.validateRussianPhoneNumberBy_E_164

internal abstract class BaseAuthViewModel : ViewModel() {

    fun isNavigateWithPhoneAble(currentPhone: String) = validateRussianPhoneNumberBy_E_164("+7$currentPhone")
}