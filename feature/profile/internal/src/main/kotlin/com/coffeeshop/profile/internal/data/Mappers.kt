package com.coffeeshop.profile.internal.data

import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.user.User
import com.coffeeshop.common.model.user.UserBonusPoints
import com.coffeeshop.common.model.user.UserEmail
import com.coffeeshop.common.model.user.UserPhone
import com.coffeeshop.common.model.user.UserRole
import com.coffeeshop.contracts.ProfileDto

fun ProfileDto.toUser(): User {
    return User(
        userId = ID(this.id),
        userName = NameModel(this.name),
        userPhone = UserPhone(this.phone),
        userEmail = this.email?.let { UserEmail(it) },
        userRole = UserRole.USER,
        userBonusPoints = UserBonusPoints(this.bonusPoints),
        notificationsEnabled = false
    )
}