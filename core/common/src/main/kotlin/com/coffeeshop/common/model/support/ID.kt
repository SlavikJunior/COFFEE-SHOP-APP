package com.coffeeshop.common.model.support

import com.coffeeshop.utils.isNotNegative

@JvmInline
value class ID(val value: Long) {

    init {

        require(value.isNotNegative()) { "ID cannot be negative" }
    }
}