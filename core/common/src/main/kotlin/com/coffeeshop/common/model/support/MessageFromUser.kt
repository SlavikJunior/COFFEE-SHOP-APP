package com.coffeeshop.common.model.support

import kotlin.time.Instant

data class MessageFromUser(
    val messageId: ID,
    val userId: ID,
    val textFromUser: TextFromUser,
    val createdAt: Instant,
)