package com.example.taskmanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskmanagement.data.local.models.QuestClaim
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestClaimDao {

    @Query("SELECT questId FROM quest_claim WHERE periodKey = :periodKey")
    fun claimedForPeriod(periodKey: String): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM quest_claim WHERE questId = :questId AND periodKey = :periodKey")
    suspend fun isClaimed(questId: String, periodKey: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun claim(claim: QuestClaim)
}