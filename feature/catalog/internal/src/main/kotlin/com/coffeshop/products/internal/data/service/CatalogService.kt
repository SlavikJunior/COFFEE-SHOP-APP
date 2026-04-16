package com.coffeshop.products.internal.data.service

import com.coffeeshop.contracts.MenuItemDetailDto
import com.coffeeshop.contracts.MenuResponse
import retrofit2.http.GET
import retrofit2.http.Path

internal interface ProductsService {

    @GET("api/menu")
    suspend fun getFullMenu(): MenuResponse

    @GET("api/menu/items/{id}")
    suspend fun getProductDetail(@Path("id") id: Long): MenuItemDetailDto
}
