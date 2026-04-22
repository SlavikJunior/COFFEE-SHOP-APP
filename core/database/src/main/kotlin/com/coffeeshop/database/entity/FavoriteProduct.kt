package com.coffeeshop.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coffeeshop.database.entity.FavoriteProduct.Companion.TABLE_NAME
import kotlin.time.Clock

@Entity(
    tableName = TABLE_NAME
)
data class FavoriteProduct(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    @ColumnInfo("product_id") val productId: Long = 0,

    @ColumnInfo("created_at") val createdAt: Long = Clock.System.now().epochSeconds,
    @ColumnInfo("deleted_at") val deletedAt: Long? = null,
) {

    companion object {
        const val TABLE_NAME = "favorite_products"
    }
}