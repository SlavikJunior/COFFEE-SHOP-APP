package com.coffeeshop.auth.internal.domain.usecase

import com.coffeeshop.common.result.Result
import com.coffeeshop.network.TokenRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IsUserLoggedInUseCaseImplTest {

    @Test
    fun `returns false when access token is null`() {
        val useCase = IsUserLoggedInUseCaseImpl(fakeRepository(accessToken = null))
        assertFalse(useCase())
    }

    @Test
    fun `returns true when access token is not null`() {
        val useCase = IsUserLoggedInUseCaseImpl(fakeRepository(accessToken = "eyJhbGciOiJSUzI1NiJ9.token"))
        assertTrue(useCase())
    }

    @Test
    fun `returns false after access token is cleared`() {
        val repo = fakeRepository(accessToken = "token")
        val useCase = IsUserLoggedInUseCaseImpl(repo)
        assertTrue(useCase())
        repo.accessToken = null
        assertFalse(useCase())
    }

    private fun fakeRepository(accessToken: String? = null): TokenRepository {
        return object : TokenRepository {
            override var accessToken: String? = accessToken
            override var refreshToken: String? = null
            override var userId: String? = null
            override val sessionExpired: SharedFlow<Unit> = MutableSharedFlow()
            override suspend fun updateToken(): Result<String> =
                throw UnsupportedOperationException("not needed in this test")
        }
    }
}
