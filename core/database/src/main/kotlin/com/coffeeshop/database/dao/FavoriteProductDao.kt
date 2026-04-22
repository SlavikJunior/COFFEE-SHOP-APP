package com.coffeeshop.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.coffeeshop.database.entity.FavoriteProduct
import com.coffeeshop.database.entity.FavoriteProduct.Companion.TABLE_NAME
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock

@Dao
interface FavoriteProductDao {

    @Query("""
        select *
        from $TABLE_NAME
        where deleted_at is null
    """)
    fun getLiveWhereDeletedAtIsNull(): Flow<List<FavoriteProduct>>

    @Upsert(entity = FavoriteProduct::class)
    suspend fun upsert(favoriteProduct: FavoriteProduct): Long

    @Query("""
        update $TABLE_NAME
        set deleted_at = :deletedAt
        where id = :id
    """)
    suspend fun softDelete(id: Long, deletedAt: Long = Clock.System.now().epochSeconds): Int

    @Query("""
        update $TABLE_NAME
        set deleted_at = :deletedAt
        where id in (:ids)
    """)
    suspend fun softDeleteMultiple(ids: List<Long>, deletedAt: Long = Clock.System.now().epochSeconds): Int

    @Delete(entity = FavoriteProduct::class)
    suspend fun delete(vararg favoriteProducts: FavoriteProduct): Int
}