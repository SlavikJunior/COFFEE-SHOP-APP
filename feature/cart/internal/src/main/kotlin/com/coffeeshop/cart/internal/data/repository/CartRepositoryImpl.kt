package com.coffeeshop.cart.internal.data.repository

import com.coffeeshop.cart.api.domain.model.CartItem
import com.coffeeshop.cart.api.domain.repository.CartRepository
import com.coffeeshop.cart.internal.data.mapper.toDomain
import com.coffeeshop.cart.internal.data.mapper.toEntity
import com.coffeeshop.cart.internal.data.service.CartService
import com.coffeeshop.cart.internal.di.FeatureCartComponentScope
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.model.support.Size
import com.coffeeshop.common.model.support.toPrice
import com.coffeeshop.common.result.Result
import com.coffeeshop.contracts.MenuItemDetailDto
import com.coffeeshop.contracts.MenuResponse
import com.coffeeshop.database.dao.CartDao
import com.coffeeshop.database.entity.CartEntity
import com.coffeeshop.di.qualifiers.DispatcherIO
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@FeatureCartComponentScope
internal class CartRepositoryImpl
@Inject constructor(
    @param:DispatcherIO private val dispatcher: CoroutineDispatcher,
    private val cartDao: CartDao,
    private val cartService: CartService,
) : CartRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + dispatcher)
    private val _initialized = CompletableDeferred<Unit>()

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    private var activeCartId: Long = -1L

    init {
        repositoryScope.launch {
            initData()
            _initialized.complete(Unit)
        }
    }

    override suspend fun initData() {
        withContext(dispatcher) {
            val dbCart = cartDao.getAllWhereDeletedAtIsNull().maxByOrNull { it.createdAt }
            if (dbCart != null) {
                activeCartId = dbCart.id
                val domainItems = dbCart.cartItemEntities.map { it.toDomain() }
                _items.update { domainItems }
            }

            try {
                val menu = cartService.getFullMenu()
                applyAvailabilityFilter(menu)
                updatePricesFromDetails()
            } catch (_: Exception) {
                // Network failure — continue with cached data
            }
        }
    }

    override suspend fun addToCart(item: CartItem) {
        _initialized.await()
        _items.update { current ->
            val existingIndex = current.indexOfFirst { it.productId == item.productId && it.uniqueCartItemID == item.uniqueCartItemID }
            if (existingIndex >= 0) {
                current.toMutableList().also { it[existingIndex] = item }
            } else {
                current + item
            }
        }
        persistCart()
    }

    override suspend fun removeFromCart(uniqueCartItemID: ID) {
        _initialized.await()
        _items.update { items -> items.filterNot { it.uniqueCartItemID == uniqueCartItemID } }
        persistCart()
    }

override fun getItems(): Flow<List<CartItem>> = _items.asStateFlow()

    override fun getTotalPrice(): Flow<Result<Price>> = _items.map { items ->
        Result.Success(items.fold(Price.emptyRublesPrice()) { acc, item -> acc + item.price })
    }

    override suspend fun persistCart() {
        withContext(dispatcher) {
            val entity = CartEntity(
                id = if (activeCartId > 0) activeCartId else 0,
                cartItemEntities = _items.value.map { it.toEntity() },
            )
            val result = cartDao.upsert(entity)
            if (result != -1L) activeCartId = result
        }
    }

    private fun applyAvailabilityFilter(menu: MenuResponse) {
        val availableIds = menu.categories.values
            .flatten()
            .filter { it.isAvailable }
            .map { it.id }
            .toSet()

        _items.update { items -> items.filter { it.productId.value in availableIds } }
    }

    private suspend fun updatePricesFromDetails() {
        val currentItems = _items.value
        if (currentItems.isEmpty()) return

        val updatedItems = mutableListOf<CartItem>()

        coroutineScope {
            for (batch in currentItems.chunked(MAX_CONCURRENT_VALIDATIONS)) {
                val batchResults = batch.map { item ->
                    async {
                        try {
                            val detail = cartService.getProductDetail(item.productId.value)
                            recalculatePrice(item, detail)
                        } catch (_: Exception) {
                            item
                        }
                    }
                }.awaitAll()
                updatedItems.addAll(batchResults)
            }
        }

        _items.update { updatedItems }
    }

    private fun recalculatePrice(item: CartItem, detail: MenuItemDetailDto): CartItem {
        val volumePrice: Price = detail.volumes
            .find { Size.entries.find { s -> s.ml == it.volumeMl } == item.size }
            ?.price
            ?.toPrice()
            ?: return item

        val modifiersCents: Int = item.selectedModifiers.sumOf { modifier ->
            val currentPrice = detail.compatibleModifiers
                .find { it.id == modifier.id.value }
                ?.price?.toPrice()
                ?: modifier.price
            currentPrice.toTotal()
        }

        val unitCents = volumePrice.toTotal() + modifiersCents
        val unitPrice = Price(firstPart = unitCents / 100, secondPart = unitCents % 100)
        return item.copy(price = unitPrice * item.quantity)
    }

    companion object {
        private const val MAX_CONCURRENT_VALIDATIONS = 5
    }
}