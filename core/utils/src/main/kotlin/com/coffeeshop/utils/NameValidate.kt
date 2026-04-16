package com.coffeeshop.utils

private val nameRegex = Regex("""^[А-ЯЁа-яёA-Za-z][А-ЯЁа-яёA-Za-z\s\-]{1,49}$""")

fun validateName(name: String) = name.matches(nameRegex)