package com.coffeeshop.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.coffeeshop.database.entity.CartEntity.Companion.TABLE_NAME
import com.coffeeshop.database.entity.CartEntity

@Dao
interface CartDao {

    @Insert(entity = CartEntity::class, onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(cartEntity: CartEntity): Long

    @Delete(entity = CartEntity::class)
    suspend fun delete(vararg cartEntities: CartEntity): Int

    @Upsert(entity = CartEntity::class)
    suspend fun upsert(cartEntity: CartEntity): Long

    @Query("""
        select *
        from $TABLE_NAME
        where deleted_at is null
    """)
    suspend fun getAllWhereDeletedAtIsNull(): List<CartEntity>
}