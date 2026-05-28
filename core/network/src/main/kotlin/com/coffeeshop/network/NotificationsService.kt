package com.coffeeshop.network

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

@Serializable
internal data class DeviceTokenRequest(val fcmToken: String)

internal interface NotificationsService {

    @POST("api/device-token")
    suspend fun registerToken(@Body request: DeviceTokenRequest)

    @DELETE("api/device-token")
    suspend fun deleteToken(@Body request: DeviceTokenRequest)
}