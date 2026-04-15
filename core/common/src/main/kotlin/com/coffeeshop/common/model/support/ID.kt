package com.coffeeshop.common.model.support

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@JvmInline
value class ID(val value: String = Uuid.random().toString())