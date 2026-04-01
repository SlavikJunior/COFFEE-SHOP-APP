package com.coffeeshop.buildconfig.internal

import com.coffeeshop.buildconfig.api.BuildConfigProvider
import java.util.concurrent.TimeUnit

class BuildConfigProviderImpl : BuildConfigProvider {
    override fun getCoffeeShopBaseUrl() = BuildConfig.COFFEE_SHOP_BASE_URL

    override fun getCallTimeOut(): Pair<Long, TimeUnit> {
        val sec = BuildConfig.CALL_TIMEOUT_SEC.toLong()
        return sec to TimeUnit.SECONDS
    }

    override fun getReadTimeOut(): Pair<Long, TimeUnit> {
        val sec = BuildConfig.READ_TIMEOUT_SEC.toLong()
        return sec to TimeUnit.SECONDS
    }

    override fun getWriteTimeOut(): Pair<Long, TimeUnit> {
        val sec = BuildConfig.WRITE_TIMEOUT_SEC.toLong()
        return sec to TimeUnit.SECONDS
    }
}