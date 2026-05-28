package com.coffeeshop.network

import com.coffeeshop.common.result.Result

interface NotificationsRepository {
    suspend fun registerToken(fcmToken: String): Result<Unit>
    suspend fun deleteToken(fcmToken: String): Result<Unit>
}