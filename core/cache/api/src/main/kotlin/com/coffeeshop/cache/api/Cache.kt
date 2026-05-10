package com.coffeeshop.cache.api

import  com.coffeeshop.common.result.Result

interface Cache<K, V> {
    suspend fun put(key: K, value: V): Result<Boolean>

    suspend fun get(key: K): Result<V>

    suspend fun remove(key: K): Result<V?>

    fun size(): Result<Int>

    fun isStoredByValue(value: V): Result<Boolean>

    fun isStoredByKey(key: K): Result<Boolean>
}