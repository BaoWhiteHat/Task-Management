package com.example.taskmanagement.data.repository

import com.example.taskmanagement.data.local.models.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TaskRepository {

    fun getAllTasks(): Flow<List<Task>>
    fun getTasksForDate(date: LocalDate): Flow<List<Task>>
    fun getDateWithTasks(startDate: LocalDate, endDate: LocalDate): Flow<List<LocalDate>>
    fun getTasksInDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Task>>

    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun refreshTasksFromServer()

}