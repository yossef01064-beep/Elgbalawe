package com.local.fatateer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY category COLLATE LOCALIZED ASC, name COLLATE LOCALIZED ASC")
    fun observeAll(): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item): Long

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("UPDATE items SET quantity = MAX(quantity + :delta, 0) WHERE id = :id")
    suspend fun changeQuantity(id: Long, delta: Int)
}
