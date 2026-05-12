package com.coffeeshop.common.exception

sealed class AuthException(override val message: String? = null) : Throwable(message) {

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