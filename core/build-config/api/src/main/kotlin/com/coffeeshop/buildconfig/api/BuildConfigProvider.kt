package com.coffeeshop.buildconfig.api

import java.util.concurrent.TimeUnit

interface BuildConfigProvider {

    fun getCoffeeShopBaseUrl(): String
    fun getCoffeeShopTestBaseUrl(): String

    fun getCallTimeOut(): Pair<Long, TimeUnit>

    fun getReadTimeOut(): Pair<Long, TimeUnit>

    fun getWriteTimeOut(): Pair<Long, TimeUnit>

    fun isDebugBuild(): Boolean
}