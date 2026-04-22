package com.coffeeshop.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.coffeeshop.database.entity.CachedProduct
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock
import com.coffeeshop.database.entity.CachedProduct.Companion.TABLE_NAME as tableName

@Dao
interface CachedProductDao {

    @Query("""
        select *
        from $tableName
        where deleted_at is null
    """)
    fun getLiveWhereDeletedAtIsNull(): Flow<List<CachedProduct>>

    @Insert(entity = CachedProduct::class, onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(cachedProduct: CachedProduct): Long

    @Delete(entity = CachedProduct::class)
    suspend fun delete(vararg cachedProducts: CachedProduct): Int

    @Query("""
        update $tableName
        set valid_to = :validTo
        where id = :id
    """)
    suspend fun updateValidTo(id: Long, validTo: Long): Int

    @Query("""
        update $tableName
        set valid_to = :validTo
        where id in (:ids)
    """)
    suspend fun updateValidToMultiple(ids: List<Long>, validTo: Long): Int

    @Query("""
        update $tableName
        set deleted_at = :deletedAt
        where id = :id
    """)
    suspend fun softDelete(id: Long, deletedAt: Long = Clock.System.now().epochSeconds): Int

    @Query("""
        update $tableName
        set deleted_at = :deletedAt
        where id in (:ids)
    """)
    suspend fun softDeleteMultiple(ids: List<Long>, deletedAt: Long = Clock.System.now().epochSeconds): Int
}