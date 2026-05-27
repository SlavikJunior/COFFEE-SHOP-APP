package com.coffeeshop.orderhistory.internal.data.repository

import android.util.Log
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.result.asErrorResult
import com.coffeeshop.common.result.asSuccessResult
import com.coffeeshop.di.qualifiers.DispatcherIO
import com.coffeeshop.orderhistory.api.domain.model.OrderSummary
import com.coffeeshop.orderhistory.api.domain.repository.OrderHistoryRepository
import com.coffeeshop.orderhistory.internal.data.mapper.toOrderSummary
import com.coffeeshop.orderhistory.internal.data.service.OrderHistoryService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class OrderHistoryRepositoryImpl
@Inject constructor(
    private val service: OrderHistoryService,
    @param:DispatcherIO private val dispatcher: CoroutineDispatcher,
) : OrderHistoryRepository {

    override suspend fun getOrderHistory(page: Int, size: Int): Result<List<OrderSummary>> =
        withContext(dispatcher) {
            try {
                service.getOrderHistory(page = page, size = size)
                    .content
                    .map { it.toOrderSummary() }
                    .asSuccessResult()
            } catch (cause: Throwable) {
                Log.e(TAG, "getOrderHistory failed: $cause")
                cause.asErrorResult()
            }
        }

    private companion object {
        const val TAG = "OrderHistoryRepo"
    }
}
