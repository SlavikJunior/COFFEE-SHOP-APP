package com.coffeeshop.common.model.user

import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.model.support.ID

data class User(
    val userId: ID,
    val userName: NameModel,
    val userPhone: UserPhone,
    val userEmail: UserEmail?,
    val userRole: UserRole,
    val userBonusPoints: UserBonusPoints,
    val notificationsEnabled: Boolean
)