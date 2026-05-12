package com.coffeeshop.database.common

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.coffeeshop.common.model.products.Category
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.model.support.Size
import kotlinx.serialization.json.Json
import javax.inject.Inject

@ProvidedTypeConverter
class TypeConverter
@Inject constructor(
    private val json: Json
) {

    @TypeConverter
    fun fromSizeToJsonString(size: Size): String = json.encodeToString(size)

    @TypeConverter
    fun fromJsonStringToSize(string: String): Size = json.decodeFromString<Size>(string)

    @TypeConverter
    fun fromSetOfSizeToJsonString(sizes: Set<Size>): String = json.encodeToString(sizes)

    @TypeConverter
    fun fromJsonStringToSetOfSize(string: String): Set<Size> = json.decodeFromString<Set<Size>>(string)

    @TypeConverter
    fun fromPriceToJsonString(price: Price): String = json.encodeToString(price)

    @TypeConverter
    fun fromJsonStringToPrice(string: String): Price = json.decodeFromString<Price>(string)

    @TypeConverter
    fun fromMapOfSizeAndPriceToJsonString(map: Map<Size, Price>): String = json.encodeToString(map)

    @TypeConverter
    fun fromJsonStringToMapOfSizeAndPrice(string: String): Map<Size, Price> = json.decodeFromString<Map<Size, Price>>(string)

    @TypeConverter
    fun fromCategoryToJsonString(category: Category): String = json.encodeToString(category)

    @TypeConverter
    fun fromJsonStringToCategory(string: String): Category = json.decodeFromString<Category>(string)
}