package com.coffeeshop.auth.internal.domain.usecase

import com.coffeeshop.auth.api.domain.repository.AuthRepository
import com.coffeeshop.auth.api.domain.usecase.SendSmsCodeByPhoneNumberUseCase
import com.coffeeshop.common.model.AuthStatus
import com.coffeeshop.common.model.PhoneNumberModel
import com.coffeeshop.common.result.Result
import javax.inject.Inject
import kotlin.time.Clock

class SendSmsCodeByPhoneNumberUseCaseImpl
@Inject constructor(
    private val authRepository: AuthRepository
) : SendSmsCodeByPhoneNumberUseCase {

    private val requestTimestamps = ArrayDeque<Long>()

    override suspend fun invoke(phoneNumber: PhoneNumberModel): Result<AuthStatus> {
        if (!checkRateLimit()) return Result.Error(Exception("Too many requests"))
        return authRepository.sendSms(phoneNumber)
    }

    // Записывает текущее время и проверяет:
    // среднее между последними 5 запросами >= 5 минут → true (разрешить)
    // среднее < 5 минут → false (заблокировать)
    private fun checkRateLimit(): Boolean {
        val now = Clock.System.now().epochSeconds
        requestTimestamps.addLast(now)
        if (requestTimestamps.size > 5) requestTimestamps.removeFirst()

        if (requestTimestamps.size < 2) return true

        val averageIntervalSec = (requestTimestamps.last() - requestTimestamps.first()).toDouble() /
                (requestTimestamps.size - 1)

        return averageIntervalSec >= FIVE_MINUTES_SEC
    }

    private companion object {
        const val FIVE_MINUTES_SEC = 5 * 60.0
    }
}