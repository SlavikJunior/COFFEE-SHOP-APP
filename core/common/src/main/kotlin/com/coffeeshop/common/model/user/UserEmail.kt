package com.coffeeshop.common.model.user

@JvmInline
value class UserEmail(val value: String) {

    init {
        require(value.matches(emailRegex)) { "User email: $value is not valid" }
    }

    companion object {
        val emailRegex = """^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$""".toRegex()
    }
}