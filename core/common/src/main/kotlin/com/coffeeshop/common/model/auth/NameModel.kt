package com.coffeeshop.common.model.auth

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class NameModel(val value: String) {
    init {
        require(value.isNotBlank()) { "Name: $value cannot be blank" }
    }
}