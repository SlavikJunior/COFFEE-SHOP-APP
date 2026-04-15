package com.coffeeshop.auth.api.domain.exception

sealed class AuthException(override val message: String? = null) : Throwable(message) {

    class ToManyRequestsException(
        override val message: String? = "Слишком много попыток. Попробуйте позже"
    ) : AuthException()

    class AlreadyRegistered(
        override val message: String? = "На этот номер телефона уже зарегистрирован аккаунт. Попробуйте другой номер."
    ) : AuthException()

    class InvalidInputDataFormat(
        override val message: String? = "Некорректный формат ввода."
    ) : AuthException()

    class PleaseWaitToVerifySmsCode(
        override val message: String? = "Пожалуйста, дождитесь проверки СМС-кода."
    ) : AuthException()
}