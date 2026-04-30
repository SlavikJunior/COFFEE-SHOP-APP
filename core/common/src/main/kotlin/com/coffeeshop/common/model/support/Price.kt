package com.coffeeshop.common.model.support

import kotlinx.serialization.Serializable

@Serializable
data class Price(
    val firstPart: Int,
    val secondPart: Int,
    val currency: Currency = Currency.RUBLES
) : Comparable<Price> {

    init {
        require(firstPart >= 0 && secondPart >= 0 && secondPart <= 99) { "Amount must be positive" }
    }

    fun display(): String = buildString {
        append(firstPart)
        if (secondPart > 0) append(",${secondPart.toString().padStart(2, '0')}")

        append(
            when (currency) {
                Currency.RUBLES -> (" ₽")
            }
        )
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
        val thisTotal = firstPart * 100L + secondPart
        val otherTotal = other.firstPart * 100L + other.secondPart
        return thisTotal.compareTo(otherTotal)
    }
}