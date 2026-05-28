package com.coffeeshop.network

import android.util.Log
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.result.asErrorResult
import com.coffeeshop.common.result.asSuccessResult
import kotlinx.coroutines.withContext
import com.coffeeshop.di.qualifiers.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

internal class NotificationsRepositoryImpl @Inject constructor(
    private val service: NotificationsService,
    @param:DispatcherIO private val dispatcher: CoroutineDispatcher,
) : NotificationsRepository {

    override suspend fun registerToken(fcmToken: String): Result<Unit> =
        withContext(dispatcher) {
            try {
                service.registerToken(DeviceTokenRequest(fcmToken))
                Unit.asSuccessResult()
            } catch (cause: Throwable) {
                Log.e(TAG, "registerToken failed", cause)
                cause.asErrorResult()
            }
        }

    override suspend fun deleteToken(fcmToken: String): Result<Unit> =
        withContext(dispatcher) {
            try {
                service.deleteToken(DeviceTokenRequest(fcmToken))
                Unit.asSuccessResult()
            } catch (cause: Throwable) {
                Log.w(TAG, "deleteToken failed (expected if backend not ready)", cause)
                cause.asErrorResult()
            }
        }

    private companion object {
        const val TAG = "NotificationsRepository"
    }
}
