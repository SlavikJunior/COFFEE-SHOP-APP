package com.coffeeshop.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coffeeshop.common.model.auth.NameModel
import com.coffeeshop.common.model.products.Category
import com.coffeeshop.common.model.products.Product
import com.coffeeshop.common.model.support.ID
import com.coffeeshop.common.model.support.Price
import com.coffeeshop.common.model.support.Size
import com.coffeeshop.database.entity.CachedProduct.Companion.TABLE_NAME
import kotlin.time.Clock

@Entity(
    tableName = TABLE_NAME
)
data class CachedProduct(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    @ColumnInfo("valid_to") val validTo: Long = Clock.System.now().epochSeconds,
    @ColumnInfo("created_at") val createdAt: Long = Clock.System.now().epochSeconds,
    @ColumnInfo("deleted_at") val deletedAt: Long? = null,

    @ColumnInfo("product_id") val productId: Long,
    @ColumnInfo("product_name") val productName: String,
    @ColumnInfo("product_description") val productDescription: String? = null,
    @ColumnInfo("product_category") val productCategory: Category,
    @ColumnInfo("product_prices") val productPrices: Map<Size, Price>,
    @ColumnInfo("product_available_sizes") val productAvailableSizes: Set<Size>,
    @ColumnInfo("product_local_image_url") val productLocalImageUrl: String? = null,
    @ColumnInfo("product_remote_image_url") val productRemoteImageUrl: String? = null,
    @ColumnInfo("product_is_available") val productIsAvailable: Boolean,
) {

    fun toDomain(): Product =
        Product(
            productId = ID(this.productId),
            productName = NameModel(this.productName),
            description = this.productDescription,
            category = this.productCategory,
            prices = this.productPrices,
            availableSizes = this.productAvailableSizes,
            imageUrl = this.productLocalImageUrl ?: this.productRemoteImageUrl,
            isAvailable = this.productIsAvailable
        )

    companion object {
        const val TABLE_NAME = "cached_products"

        fun fromDomain(product: Product): CachedProduct = with(product) {
            CachedProduct(
                productId = productId.value,
                productName = productName.value,
                productCategory = category,
                productPrices = prices,
                productAvailableSizes = availableSizes,
                productLocalImageUrl = null,
                productRemoteImageUrl = imageUrl,
                productIsAvailable = isAvailable
            )
        }
    }
}