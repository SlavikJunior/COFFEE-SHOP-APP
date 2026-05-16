package com.coffeeshop.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.model.support.Size
import com.coffeeshop.database.entity.CartEntity.Companion.TABLE_NAME
import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Entity(tableName = TABLE_NAME)
data class CartEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo("created_at") val createdAt: Long = Clock.System.now().epochSeconds,
    @ColumnInfo("deleted_at") val deletedAt: Long? = null,

    @ColumnInfo("cart_items") val cartItemEntities: List<CartItemEntity> = emptyList()
) {

    companion object {
        const val TABLE_NAME = "cart"
    }
}

@Serializable
data class CartItemModifierEntity(
    val id: Long = 0,
    val name: String = "",
    val price: Price = Price.emptyRublesPrice(),
    val category: String = "",
)

@Serializable
data class CartItemEntity(
    val uniqueCartItemID: Long = 0,
    val productId: Long = 0,
    val productName: String = "",
    val imageUrl: String? = null,
    val price: Price = Price.emptyRublesPrice(),
    val size: Size = Size.MEDIUM,
    val quantity: Int = 0,
    val comment: String = "",
    val modifiers: List<CartItemModifierEntity> = emptyList(),
)