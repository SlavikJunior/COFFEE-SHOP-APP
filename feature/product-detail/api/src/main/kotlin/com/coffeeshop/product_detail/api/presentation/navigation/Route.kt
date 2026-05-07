package com.coffeeshop.product_detail.api.presentation.navigation

import com.coffeeshop.common.model.support.ID
import com.coffeshop.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class ProductDetailRoute(val productID: ID) : Route