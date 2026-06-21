package com.example.taskmanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskmanagement.data.local.models.Achievement
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {

    @Query("SELECT * FROM achievements ORDER BY sortOrder ASC")
    fun getAll(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements ORDER BY sortOrder ASC")
    suspend fun getAllOnce(): List<Achievement>

    @Query("SELECT COUNT(*) FROM achievements")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    fun unlockedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<Achievement>)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :time WHERE id = :id AND isUnlocked = 0")
    suspend fun unlock(id: String, time: Long)
}