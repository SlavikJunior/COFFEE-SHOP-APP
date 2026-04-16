package com.coffeeshop.common.exception

sealed class BaseException(override val message: String? = null): Exception(message) {

    class ToManyRequestsException(
        override val message: String? = "Слишком много попыток. Попробуйте позже"
    ) : BaseException()
}