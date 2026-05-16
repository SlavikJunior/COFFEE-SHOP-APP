package com.coffeeshop.logger.api

@JvmInline
value class Tag(val value: String) {

    init {
        require(value.isNotBlank()) { "Tag cannot be blank!" }
    }
}

fun String.tagOf(): Tag = Tag(this)