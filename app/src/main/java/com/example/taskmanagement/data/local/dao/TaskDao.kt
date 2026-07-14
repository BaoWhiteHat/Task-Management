package com.example.taskmanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.taskmanagement.data.local.models.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE syncStatus != 'DELETED' ORDER BY isCompleted ASC, dueDate DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate = :today AND isCompleted = 0 AND syncStatus != 'DELETED'")
    fun getTasksDueToday(today: LocalDate): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate = :date AND syncStatus != 'DELETED' ORDER BY isCompleted ASC")
    fun getTasksForDate(date: LocalDate): Flow<List<Task>>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1 AND syncStatus != 'DELETED'")
    fun getCompletedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0 AND dueDate < :today AND syncStatus != 'DELETED'")
    fun getOverDueCount(today: LocalDate): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE priority = 'High' AND isCompleted = 0 AND syncStatus != 'DELETED'")
    fun getPendingHighPriorityCount(): Flow<Int>

    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startDate AND :endDate AND syncStatus != 'DELETED'")
    fun getTasksInDateRange(startDate: LocalDate,endDate: LocalDate): Flow<List<Task>>

    @Query("SELECT DISTINCT dueDate FROM tasks WHERE dueDate BETWEEN :startDate AND :endDate AND syncStatus != 'DELETED'")
    fun getDatesWithTasks(startDate: LocalDate,endDate: LocalDate): Flow<List<LocalDate>>

    @Query("SELECT * FROM tasks WHERE syncStatus != 'SYNCED'")
    suspend fun getDirtyTasks(): List<Task>

    @Query("SELECT id FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun findExistingId(taskId: Int): Int?

    @Query("SELECT * FROM tasks WHERE id = :taskId AND syncStatus != 'DELETED' LIMIT 1")
    suspend fun getTaskById(taskId: Int): Task?

    // crud Op
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(tasks: List<Task>)

    @Insert(onConflict = REPLACE)
    suspend fun upsertAll(tasks: List<Task>)

}
