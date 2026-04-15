package com.coffeeshop.common.model.support

@JvmInline
value class Name(val value: String) {
    init {
        require(value.isNotBlank()) { "Username: $value cannot be blank" }
    }
}