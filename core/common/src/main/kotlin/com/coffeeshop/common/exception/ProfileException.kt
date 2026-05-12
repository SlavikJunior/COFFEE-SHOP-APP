package com.coffeeshop.common.exception

sealed class ProfileException(override val message: String? = null) : BaseException(message)