package com.coffeeshop.common.model.user

import com.coffeeshop.utils.isNoLessThan
import com.coffeeshop.utils.isNotNegative

@JvmInline
value class UserBonusPoints(val value: Int) {

    init {
        require(value.isNotNegative()) { "User bonus cannot be negative" }
    }

    operator fun plus(other: UserBonusPoints) = this.value + other.value

    operator fun minus(other: UserBonusPoints): Int {
        require(this.value.isNoLessThan(other.value)) { "Unable to minus" }

        return this.value - other.value
    }

    operator fun times(other: Int): Int = this.value * other

    operator fun times(other: UserBonusPoints): Int = this.times(other.value)
}