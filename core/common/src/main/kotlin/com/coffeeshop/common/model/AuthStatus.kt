package com.coffeeshop.common.model

sealed interface AuthStatus {
    object WaitSms : AuthStatus
    object Guest : AuthStatus
    object User : AuthStatus
    object Banned : AuthStatus
}