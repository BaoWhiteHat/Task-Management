package com.example.taskmanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskmanagement.data.local.models.GameProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface GameProfileDao {

    @Query("SELECT * FROM game_profile WHERE id = 1")
    fun getProfile(): Flow<GameProfile?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createProfile(profile: GameProfile = GameProfile())

    @Update
    suspend fun updateProfile(profile: GameProfile)

    @Query("UPDATE game_profile SET xp = xp + :amount WHERE id = 1")
    suspend fun addXp(amount: Int)

    @Query("UPDATE game_profile SET coins = coins + :amount WHERE id = 1")
    suspend fun addCoins(amount: Int)

    @Query("UPDATE game_profile SET coins = coins - :amount WHERE id = 1")
    suspend fun spendCoins(amount: Int)

    @Query("UPDATE game_profile SET level = level + 1, xp = xp - :overflow WHERE id = 1")
    suspend fun levelUp(overflow: Int)
}