package com.coffeeshop.network

import com.coffeeshop.common.result.Result
import kotlinx.coroutines.flow.SharedFlow

interface TokenRepository {

    var accessToken: String?
    var refreshToken: String?
    var userId: String?

    val sessionExpired: SharedFlow<Unit>

    suspend fun updateToken(): Result<String>
}