package com.coffeeshop.profile.api.domain.usecase

import com.coffeeshop.common.model.support.FeedBackStatus
import com.coffeeshop.common.model.support.MessageFromUser
import com.coffeeshop.common.result.Result
import kotlinx.coroutines.flow.Flow

interface SendFeedBackUseCase {

    suspend operator fun invoke(messageFromUser: MessageFromUser): Flow<Result<FeedBackStatus>>
}