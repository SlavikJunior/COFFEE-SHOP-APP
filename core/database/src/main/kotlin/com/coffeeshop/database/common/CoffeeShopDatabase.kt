package com.coffeeshop.database.common

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.coffeeshop.database.common.CoffeeShopDatabase.Companion.COFFEE_SHOP_DATABASE_VERSION
import com.coffeeshop.database.dao.CachedProductDao
import com.coffeeshop.database.dao.FavoriteProductDao
import com.coffeeshop.database.entity.CachedProduct
import com.coffeeshop.database.entity.FavoriteProduct

@Database(
    entities = [
        FavoriteProduct::class,
        CachedProduct::class
    ],
    version = COFFEE_SHOP_DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(TypeConverter::class)
abstract class CoffeeShopDatabase : RoomDatabase() {

    abstract val favoriteProductDao: FavoriteProductDao

    abstract val cachedProductDao: CachedProductDao

    companion object {
        const val COFFEE_SHOP_DATABASE_NAME = "CoffeeShopDatabase"
        private const val COFFEE_SHOP_DATABASE_VERSION = 1
    }
}