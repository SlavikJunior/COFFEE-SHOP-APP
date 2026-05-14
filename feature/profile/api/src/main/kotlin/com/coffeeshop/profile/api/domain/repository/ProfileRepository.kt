package com.coffeeshop.profile.api.domain.repository

import com.coffeeshop.common.model.auth.AuthStatus
import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.model.auth.PhoneNumberModel
import com.coffeeshop.common.model.order.Order
import com.coffeeshop.common.model.support.FeedBackStatus
import com.coffeeshop.common.model.support.MessageFromUser
import com.coffeeshop.common.model.user.User
import com.coffeeshop.common.model.user.UserEmail
import com.coffeeshop.common.model.user.UserNotificationsEnabled
import com.coffeeshop.common.result.Result
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    suspend fun getProfile(): Result<User>

    // возвращаем новый email
    suspend fun changeEmail(newEmail: UserEmail): Result<UserEmail>

    // возвраащем новое имя
    suspend fun changeName(newName: NameModel): Result<NameModel>

    suspend fun changePhoneNumber(newPhoneNumber: PhoneNumberModel): Result<PhoneNumberModel>

    suspend fun getOrderHistory(): Flow<Result<List<Order>>>

    suspend fun logout(): Result<AuthStatus>

    suspend fun sendFeedBack(messageFromUser: MessageFromUser): Flow<Result<FeedBackStatus>>

    suspend fun toggleGetNotifications(): Result<UserNotificationsEnabled>
}