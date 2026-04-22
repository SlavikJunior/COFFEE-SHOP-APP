package com.coffeeshop.network.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.coffeeshop.di.qualifiers.ApplicationContext
import com.coffeeshop.di.qualifiers.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TokenStorage
@Inject constructor(
    @ApplicationContext context: Context,
    @DispatcherIO private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREF_FILE_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var accessToken: String?
        get() = prefs.getString(ACCESS_TOKEN_KEY, null)
        set(value) = prefs.edit { putString(ACCESS_TOKEN_KEY, value) }

    var refreshToken: String?
        get() = prefs.getString(REFRESH_TOKEN_KEY, null)
        set(value) = prefs.edit { putString(REFRESH_TOKEN_KEY, value) }

    suspend fun update(
        accessToken: String,
        refreshToken: String
    ) {
        withContext(dispatcher) {
            this@TokenStorage.accessToken = accessToken
            this@TokenStorage.refreshToken = refreshToken
        }
    }

    fun clear() = prefs.edit { clear() }

    private companion object {
        const val PREF_FILE_NAME = "auth_secure"
        const val ACCESS_TOKEN_KEY = "access_token"
        const val REFRESH_TOKEN_KEY = "refresh_token"
    }
}
