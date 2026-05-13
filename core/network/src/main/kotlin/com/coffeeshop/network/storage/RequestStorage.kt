package com.coffeeshop.network.storage

import android.content.Context
import androidx.core.content.edit
import com.coffeeshop.common.exception.BaseException
import com.coffeeshop.di.qualifiers.ApplicationContext
import com.coffeeshop.utils.orZero
import javax.inject.Inject
import kotlin.time.Clock

class RequestStorage
@Inject constructor(
    @ApplicationContext context: Context
) {

    private val prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)

    private var lastRequestTime: Long?
        get() = prefs.getLong(LAST_REQUEST_TIME_KEY, 0)
        set(value) = prefs.edit { putLong(LAST_REQUEST_TIME_KEY, value.orZero()) }



    fun makeRequest() {
        val now: Long = Clock.System.now().toEpochMilliseconds()
        if (now - (lastRequestTime.orZero<Long?, Long>()) >= MIN_DIF) {
            lastRequestTime = now
        } else throw BaseException.ToManyRequestsException()
    }

    fun clear() = prefs.edit { clear() }

    private companion object {
        const val MIN_DIF = 1000 * 60 * 5
        const val PREF_FILE_NAME = "req"
        const val LAST_REQUEST_TIME_KEY = "access_token"
    }
}