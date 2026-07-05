package com.example.taskmanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.taskmanagement.data.local.models.LootInventoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface LootInventoryDao {

    @Query("SELECT * FROM loot_inventory WHERE count > 0 ORDER BY itemId ASC")
    fun getOwned(): Flow<List<LootInventoryItem>>

    @Query("SELECT COUNT(*) FROM loot_inventory WHERE count > 0")
    fun distinctOwnedCount(): Flow<Int>

    @Query("SELECT * FROM loot_inventory WHERE itemId = :itemId LIMIT 1")
    suspend fun get(itemId: String): LootInventoryItem?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(item: LootInventoryItem)

    @Query("UPDATE loot_inventory SET count = count + :n WHERE itemId = :itemId")
    suspend fun increment(itemId: String, n: Int)

    @Query("UPDATE loot_inventory SET count = count - :n WHERE itemId = :itemId")
    suspend fun decrement(itemId: String, n: Int)

    @Query(
        "UPDATE loot_inventory SET count = count - 1 " +
            "WHERE itemId = :itemId AND count > 0"
    )
    suspend fun decrementIfOwned(itemId: String): Int

    @Query("DELETE FROM loot_inventory WHERE count <= 0")
    suspend fun purgeEmpty()

    @Transaction
    suspend fun addItem(itemId: String, n: Int = 1, profileId: Int = 1) {
        insertIfAbsent(LootInventoryItem(itemId = itemId, count = 0, profileId = profileId))
        increment(itemId, n)
    }

    @Transaction
    suspend fun removeItem(itemId: String, n: Int = 1) {
        decrement(itemId, n)
        purgeEmpty()
    }

    @Transaction
    suspend fun consumeOne(itemId: String): Boolean {
        val consumed = decrementIfOwned(itemId) > 0
        if (consumed) purgeEmpty()
        return consumed
    }
}
