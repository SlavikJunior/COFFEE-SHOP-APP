package com.coffeeshop.profile.internal.domain.usecase

import com.coffeeshop.common.model.support.FeedBackStatus
import com.coffeeshop.common.model.support.MessageFromUser
import com.coffeeshop.common.result.Result
import com.coffeeshop.profile.api.domain.repository.ProfileRepository
import com.coffeeshop.profile.api.domain.usecase.SendFeedBackUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendFeedBackUseCaseImpl
@Inject constructor(
    private val repository: ProfileRepository
) : SendFeedBackUseCase {

    override suspend operator fun invoke(messageFromUser: MessageFromUser): Flow<Result<FeedBackStatus>> = repository.sendFeedBack(messageFromUser)
}