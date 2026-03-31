package com.coffeshop.utils

private val russianPhoneRegex = Regex("^\\+7\\d{10}$")

fun validateRussianPhoneNumberBy_E_164(phoneNumber: String) = phoneNumber.matches(russianPhoneRegex)