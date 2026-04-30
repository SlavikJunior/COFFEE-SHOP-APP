package com.coffeeshop.common.model.support

@JvmInline
value class TextFromUser(val value: String) {

    init {
        require(isValidText()) { "Text is invalid" }
    }

    // TODO("Добавить валидацию текста, пока просто заглушка"
    private fun isValidText(): Boolean = value.isNotEmpty()
}