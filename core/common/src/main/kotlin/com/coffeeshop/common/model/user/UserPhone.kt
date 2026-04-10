package com.coffeeshop.common.model.user

@JvmInline
value class UserPhone(val value: String) {

    init {
        require(value.matches(phoneRegex)) { "User phone: $value is not valid" }
    }

    companion object {
        val phoneRegex = """^(?:\+?7|8)?[\s\-]?\(?\d{3}\)?[\s\-]?\d{3}[\s\-]?\d{2}[\s\-]?\d{2}$""".toRegex()
    }
}