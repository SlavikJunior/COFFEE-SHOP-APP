package com.coffeeshop.auth.api.domain.repository

import com.coffeeshop.common.model.auth.AuthStatus
import com.coffeeshop.common.result.Result

interface AuthRepository {

    suspend fun verifyFirebaseToken(idToken: String): Result<Boolean>

    suspend fun registerWithFirebase(idToken: String, name: String): Result<AuthStatus>

    suspend fun sendNewToken(token: String)

    suspend fun deleteToken(token: String)
}