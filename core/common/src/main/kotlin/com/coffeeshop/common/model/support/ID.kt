package com.coffeeshop.common.model.support

import com.coffeeshop.utils.isNotNegative
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ID(val value: Long) {

    init {

        require(value.isNotNegative()) { "ID cannot be negative" }
    }
}