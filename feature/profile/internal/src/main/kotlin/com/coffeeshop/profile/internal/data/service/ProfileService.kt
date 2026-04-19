package com.coffeeshop.profile.internal.data.service

import com.coffeeshop.contracts.ProfileDto
import com.coffeeshop.contracts.UpdateProfileRequest
import retrofit2.http.GET
import retrofit2.http.PATCH

interface ProfileService {

    // TODO(добавить аргумент в метод. контроллер на бекенде ожидает UserPrincipal)
    @GET("api/profile")
    suspend fun getProfile(): ProfileDto

    @PATCH("api/profile")
    suspend fun updateProfile(
        updateProfileRequest: UpdateProfileRequest
    ): ProfileDto
}