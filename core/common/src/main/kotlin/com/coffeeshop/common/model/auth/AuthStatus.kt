package com.coffeeshop.common.model.auth

sealed interface AuthStatus {
    object WaitSms : AuthStatus
    object Guest : AuthStatus
    object User : AuthStatus
    object Banned : AuthStatus
}