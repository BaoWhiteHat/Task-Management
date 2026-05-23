package com.example.taskmanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.taskmanagement.data.local.models.FocusSession
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface FocusSessionDao {

    @Insert
    suspend fun insertFocusSession(session: FocusSession)

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE completedDate = :date")
    fun getCompletedCountForDate(date: LocalDate): Flow<Int>

    @Query("SELECT * FROM focus_sessions ORDER BY completedAtMillis DESC")
    fun getAllFocusSessions(): Flow<List<FocusSession>>
}