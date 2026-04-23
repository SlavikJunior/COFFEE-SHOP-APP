package com.coffeeshop.utils

@Suppress("UNCHECKED_CAST")
fun <T : Number?, R : Number> T.orZero(): R = (this ?: 0) as R

fun Number.isPositive(): Boolean = this > 0

fun Number.isNotNegative(): Boolean = this >= 0

fun Number.isMoreThan(other: Number): Boolean = this > other

fun Number.isNoLessThan(other: Number): Boolean = this >= other

operator fun Number.compareTo(other: Number): Int = this.toDouble().compareTo(other.toDouble())

@Suppress("UNCHECKED_CAST")
inline fun <reified K, T> Iterable<T>.groupBy(): Map<K, List<T>> {
    return groupBy { element ->
        requireNotNull(element)
        val clazz = element::class.java
        val field = clazz.declaredFields.firstOrNull { it.type == K::class.java }
        if (field != null) {
            field.isAccessible = true
            return@groupBy field.get(element) as K
        }
        // computed Kotlin properties don't have backing fields — search getter methods
        val getter = clazz.declaredMethods.firstOrNull {
            it.returnType == K::class.java && it.parameterCount == 0
        } ?: error("No field or getter of type '${K::class.simpleName}' found in '${clazz.simpleName}'")
        getter.isAccessible = true
        getter.invoke(element) as K
    }
}