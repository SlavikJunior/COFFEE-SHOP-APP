package com.coffeeshop.auth.api.domain.usecase

interface IsUserLoggedInUseCase {
    operator fun invoke(): Boolean
}
