package com.example.taskmanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.taskmanagement.data.local.models.ProfileTome
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileTomeDao {

    @Query("SELECT * FROM profile_tomes WHERE profileId = :profileId ORDER BY tomeId")
    fun observeAll(profileId: Int): Flow<List<ProfileTome>>

    @Query("SELECT count FROM profile_tomes WHERE profileId = :profileId AND tomeId = :tomeId LIMIT 1")
    suspend fun getTomeCount(profileId: Int, tomeId: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profileTome: ProfileTome)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfMissing(profileTome: ProfileTome): Long

    @Query("UPDATE profile_tomes SET count = count + 1 WHERE profileId = :profileId AND tomeId = :tomeId")
    suspend fun incrementExisting(profileId: Int, tomeId: String): Int

    @Transaction
    suspend fun increment(profileId: Int, tomeId: String) {
        val inserted = insertIfMissing(ProfileTome(profileId = profileId, tomeId = tomeId, count = 1))
        if (inserted == -1L) incrementExisting(profileId, tomeId)
    }

    @Query(
        "UPDATE profile_tomes SET count = count - 1 " +
            "WHERE profileId = :profileId AND tomeId = :tomeId AND count > 0"
    )
    suspend fun decrementExisting(profileId: Int, tomeId: String): Int

    @Query("DELETE FROM profile_tomes WHERE profileId = :profileId AND tomeId = :tomeId AND count <= 0")
    suspend fun deleteEmpty(profileId: Int, tomeId: String)

    @Transaction
    suspend fun decrement(profileId: Int, tomeId: String): Boolean {
        val changed = decrementExisting(profileId, tomeId) > 0
        if (changed) deleteEmpty(profileId, tomeId)
        return changed
    }
}
