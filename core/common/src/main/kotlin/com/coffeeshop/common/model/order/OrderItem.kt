package com.coffeeshop.common.model.order

import com.coffeeshop.common.model.products.Modifier
import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.model.support.Size

data class OrderItem(
    val orderItemId: ID,
    val product: Product,
    val size: Size,
    val quantity: Int,
    val modifiers: List<Modifier>,
) {

    val totalPrice: Price
        get() {
            val basePrice: Price = product.prices[size] ?: Price(0, 0)
            val additivesPrice: Price = modifiers.fold(Price(0, 0)) { acc, additive ->
                acc + additive.price
            }
            return (basePrice + additivesPrice) * quantity
        }
}