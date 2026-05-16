package com.coffeeshop.logger.api

interface CoffeeshopLogger {

    fun info(tag: Tag? = null, message: String)
    fun debug(tag: Tag? = null, message: String)
    fun warning(tag: Tag? = null, message: String)
    fun error(tag: Tag? = null, message: String)
}