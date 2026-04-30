package com.coffeeshop.common.model.support

import com.coffeeshop.utils.isNotNegative
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
@JvmInline
value class ID(val value: Long) {

    init {

        require(value.isNotNegative()) { "ID cannot be negative" }
    }

    companion object {
        fun random(): ID {
            return ID(
                value = Random.nextLong(from = 0, until = Long.MAX_VALUE)
            )
        }
    }
}