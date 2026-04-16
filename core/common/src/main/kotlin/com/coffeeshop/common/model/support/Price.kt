package com.coffeeshop.common.model.support

data class Price(
    val firstPart: Int,
    val secondPart: Int,
    val currency: Currency = Currency.RUBLES
): Comparable<Price> {

    init {
        require(firstPart >= 0 && secondPart >= 0 && secondPart <= 99) { "Amount must be positive" }
    }

    operator fun plus(other: Price): Price {
        val totalSeconds = secondPart + other.secondPart
        val carry = totalSeconds / 100
        return Price(
            firstPart = firstPart + other.firstPart + carry,
            secondPart = totalSeconds % 100,
            currency = currency,
        )
    }

    operator fun times(multiplier: Int): Price {
        val totalSeconds = secondPart * multiplier
        val carry = totalSeconds / 100
        return Price(
            firstPart = firstPart * multiplier + carry,
            secondPart = totalSeconds % 100,
            currency = currency,
        )
    }

    override fun compareTo(other: Price): Int {
        return when {
            this > other -> 1
            this < other -> -1
            this == other -> 0
            else -> Int.MAX_VALUE
        }
    }
}