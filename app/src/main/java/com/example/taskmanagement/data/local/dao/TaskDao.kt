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
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDate DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate = :today AND isCompleted = 0")
    fun getTasksDueToday(today: LocalDate): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate = :date ORDER BY isCompleted ASC")
    fun getTasksForDate(date: LocalDate): Flow<List<Task>>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0 AND dueDate < :today")
    fun getOverDueCount(today: LocalDate): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks  WHERE priority = 'High' AND isCompleted = 0")
    fun getPendingHighPriorityCount(): Flow<Int>

    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startDate AND :endDate")
    fun getTasksInDateRange(startDate: LocalDate,endDate: LocalDate): Flow<List<Task>>

    @Query("SELECT DISTINCT dueDate FROM tasks WHERE dueDate BETWEEN :startDate AND :endDate")
    fun getDatesWithTasks(startDate: LocalDate,endDate: LocalDate): Flow<List<LocalDate>>

    @Query("SELECT * FROM tasks WHERE syncStatus != 'SYNCED'")
    suspend fun getDirtyTasks(): List<Task>
    // crud Op
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(tasks: List<Task>)

    @Insert(onConflict = REPLACE)
    suspend fun upsertAll(tasks: List<Task>)

}