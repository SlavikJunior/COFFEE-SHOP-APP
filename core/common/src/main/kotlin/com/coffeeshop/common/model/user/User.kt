package com.coffeeshop.common.model.user

import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Name
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
data class User(
    val userId: ID,
    val userName: Name,
    val userPhone: UserPhone,
    val userEmail: UserEmail?,
    val userRole: UserRole,
    val notificationsEnabled: Boolean
)