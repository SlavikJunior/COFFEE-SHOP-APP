package com.coffeeshop.cart.internal.domain.usecase

import com.coffeeshop.cart.api.domain.repository.CartRepository
import com.coffeeshop.cart.api.domain.usecase.GetTotalPriceFromCartUseCase
import com.coffeeshop.common.model.support.Currency
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.result.Result
import io.mockk.Awaits
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class GetTotalPriceFromCartUseCaseImplTest {

    @MockK
    private lateinit var cartRepository: CartRepository
    private var spyUseCase: GetTotalPriceFromCartUseCase? = null

    @BeforeEach
    fun setUp() {
        spyUseCase = GetTotalPriceFromCartUseCaseImpl(cartRepository)
    }

    @AfterEach
    fun tearDown() {
        spyUseCase = null
    }

    @Test
    fun `invoke get total price from cart repository`() {
        val expectedFlow = flowOf(
            Result.Success<Price>(
                mockk {
                    every { firstPart } returns 52
                    every { secondPart } returns 67
                    every { currency } returns Currency.RUBLES
                }
            ),
        )

        coEvery {
            cartRepository.getTotalPrice()
        } returns expectedFlow

        val result = spyUseCase?.invoke()

        assert(spyUseCase?.invoke() == expectedFlow)
    }
}
