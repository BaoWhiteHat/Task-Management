package com.example.taskmanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskmanagement.data.local.models.ProfileBackground
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileBackgroundDao {

    @Query(
        "SELECT backgroundId FROM profile_backgrounds " +
            "WHERE profileId = :profileId ORDER BY backgroundId"
    )
    fun observeUnlockedBackgrounds(profileId: Int): Flow<List<String>>

    @Query(
        "SELECT EXISTS(SELECT 1 FROM profile_backgrounds " +
            "WHERE profileId = :profileId AND backgroundId = :backgroundId)"
    )
    suspend fun isUnlocked(profileId: Int, backgroundId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlock(profileBackground: ProfileBackground): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefaults(profileBackgrounds: List<ProfileBackground>)
}
