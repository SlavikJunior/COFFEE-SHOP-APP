package com.coffeeshop.profile.internal.data.repository

import android.util.Log
import com.coffeeshop.common.model.auth.AuthStatus
import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.model.auth.PhoneNumberModel
import com.coffeeshop.common.model.order.Order
import com.coffeeshop.common.model.user.User
import com.coffeeshop.common.model.user.UserEmail
import com.coffeeshop.common.model.user.UserNotificationsEnabled
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.result.asErrorResult
import com.coffeeshop.common.result.asSuccessResult
import com.coffeeshop.contracts.UpdateProfileRequest
import com.coffeeshop.profile.api.domain.repository.ProfileRepository
import com.coffeeshop.profile.internal.data.service.ProfileService
import com.coffeeshop.profile.internal.data.toUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileRepositoryImpl
@Inject constructor(
    private val profileService: ProfileService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProfileRepository {

    // TODO(добавить аргумент в метод. контроллер на бекенде ожидает UserPrincipal) Т.Е. нужно как то хранить эту информацию на устройстве
    override suspend fun getProfile(): Result<User> = withContext(dispatcher) {
        try {
            profileService.getProfile().toUser().asSuccessResult()
        } catch (cause: Throwable) {
            Log.e(TAG, "Exception in getProfile method, cause: $cause")
            cause.asErrorResult()
        }
    }

    override suspend fun changeEmail(newEmail: UserEmail): Result<UserEmail> =
        withContext(dispatcher) {
            try {
                profileService.updateProfile(
                    updateProfileRequest = UpdateProfileRequest(
                        email = newEmail.value
                    )
                )
                    .toUser().userEmail!!.asSuccessResult() // as not null потому что после обновления email точно не равен null
            } catch (cause: Throwable) {
                Log.e(TAG, "Exception in changeEmail method, cause: $cause")
                cause.asErrorResult()
            }
        }

    override suspend fun changeName(newName: NameModel): Result<NameModel> =
        withContext(dispatcher) {
            try {
                profileService.updateProfile(
                    updateProfileRequest = UpdateProfileRequest(
                        name = newName.value
                    )
                ).toUser().userName.asSuccessResult()
            } catch (cause: Throwable) {
                Log.e(TAG, "Exception in changeName method, cause: $cause")
                cause.asErrorResult()
            }
        }

    override suspend fun changePhoneNumber(newPhoneNumber: PhoneNumberModel): Result<PhoneNumberModel> {
        TODO("Not yet implemented")
    }

    override suspend fun getOrderHistory(): Result<List<Order>> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): Result<AuthStatus> {
        TODO("Not yet implemented")
    }

    override suspend fun toggleGetNotifications(): Result<UserNotificationsEnabled> {
        TODO("Not yet implemented")
    }

    private companion object {
        const val TAG = "TEST TAG"
    }
}