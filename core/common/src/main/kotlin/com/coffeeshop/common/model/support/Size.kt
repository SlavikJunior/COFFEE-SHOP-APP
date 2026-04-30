package com.coffeeshop.common.model.support

enum class Size(val ml: Int) {
    SMALL(250),
    MEDIUM(350),
    LARGE(450),
}

fun Size.display(): String = "$ml мл"