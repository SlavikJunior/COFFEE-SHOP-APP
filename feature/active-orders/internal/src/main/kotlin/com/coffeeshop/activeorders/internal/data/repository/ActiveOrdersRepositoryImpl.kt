package com.coffeeshop.activeorders.internal.data.repository

import android.util.Log
import com.coffeeshop.activeorders.api.domain.model.ActiveOrder
import com.coffeeshop.activeorders.api.domain.repository.ActiveOrdersRepository
import com.coffeeshop.activeorders.internal.data.mapper.toActiveOrder
import com.coffeeshop.activeorders.internal.data.service.ActiveOrdersService
import com.coffeeshop.common.result.Result
import com.coffeeshop.common.result.asErrorResult
import com.coffeeshop.common.result.asSuccessResult
import com.coffeeshop.di.qualifiers.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class ActiveOrdersRepositoryImpl
@Inject constructor(
    private val service: ActiveOrdersService,
    @param:DispatcherIO private val dispatcher: CoroutineDispatcher,
) : ActiveOrdersRepository {

    override suspend fun getActiveOrders(): Result<List<ActiveOrder>> =
        withContext(dispatcher) {
            try {
                val summaries = service.getActiveOrders()
                val details = summaries.map { summary ->
                    async { service.getOrderDetail(summary.id) }
                }.awaitAll()
                details.map { it.toActiveOrder() }.asSuccessResult()
            } catch (cause: Throwable) {
                Log.e(TAG, "getActiveOrders failed: $cause")
                cause.asErrorResult()
            }
        }

    private companion object {
        const val TAG = "ActiveOrdersRepo"
    }
}
