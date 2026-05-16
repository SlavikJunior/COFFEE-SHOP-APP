package com.coffeeshop.logger.internal.impl

import android.util.Log
import com.coffeeshop.logger.api.CoffeeshopLogger
import com.coffeeshop.logger.api.Tag
import javax.inject.Inject

class AndroidCoffeeshopLoggerImpl
@Inject constructor() : CoffeeshopLogger {

    override fun info(tag: Tag?, message: String) {
        Log.i(tag?.value, message)
    }

    override fun debug(tag: Tag?, message: String) {
        Log.d(tag?.value, message)
    }

    override fun warning(tag: Tag?, message: String) {
        Log.w(tag?.value, message)
    }

    override fun error(tag: Tag?, message: String) {
        Log.e(tag?.value, message)
    }
}