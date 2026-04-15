package com.coffeeshop.network.storage

import android.content.Context
import androidx.core.content.edit
import com.coffeeshop.di.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenStorage
@Inject constructor(
    @ApplicationContext context: Context
) {

    private val prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)

    var accessToken: String?
        get() = prefs.getString(ACCESS_TOKEN_KEY, null)
        set(value) = prefs.edit { putString(ACCESS_TOKEN_KEY, value) }

    var refreshToken: String?
        get() = prefs.getString(REFRESH_TOKEN_KEY, null)
        set(value) = prefs.edit { putString(REFRESH_TOKEN_KEY, value) }

    fun clear() = prefs.edit { clear() }

    private companion object {
        const val PREF_FILE_NAME = "auth"
        const val ACCESS_TOKEN_KEY = "access_token"
        const val REFRESH_TOKEN_KEY = "refresh_token"
    }
}