package com.example.taskmanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskmanagement.data.local.models.ProfileSound
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileSoundDao {

    @Query("SELECT soundId FROM profile_sounds WHERE profileId = :profileId ORDER BY soundId")
    fun observeUnlockedSounds(profileId: Int): Flow<List<String>>

    @Query(
        "SELECT EXISTS(SELECT 1 FROM profile_sounds " +
            "WHERE profileId = :profileId AND soundId = :soundId)"
    )
    suspend fun isUnlocked(profileId: Int, soundId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlock(profileSound: ProfileSound): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefaults(profileSounds: List<ProfileSound>)
}
