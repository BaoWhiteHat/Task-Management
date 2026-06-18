package com.example.taskmanagement.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.taskmanagement.data.TaskMapper
import com.example.taskmanagement.data.local.dao.TaskDao
import com.example.taskmanagement.data.local.models.SyncStatus
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.data.remote.TaskApiService
import com.example.taskmanagement.data.worker.SyncWorker
import com.example.taskmanagement.reminder.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId

private val TAG = "TaskRepositoryImpl"

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val apiService: TaskApiService,
    private val workManager: WorkManager,
    private val appContext: Context
) : TaskRepository {
    override fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    override fun getTasksForDate(date: LocalDate): Flow<List<Task>> =
        taskDao.getTasksForDate(date)

    override fun getDateWithTasks(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<LocalDate>> =
        taskDao.getDatesWithTasks(startDate, endDate)


    override fun getTasksInDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<Task>> = taskDao.getTasksInDateRange(startDate, endDate)

    override suspend fun insertTask(task: Task) {
        val newId = taskDao.insertTask(task.copy(syncStatus = SyncStatus.CREATED))
        applyReminder(task.copy(id = newId.toInt()))
        scheduleSyc()
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.copy(syncStatus = SyncStatus.UPDATED))
        applyReminder(task)
        scheduleSyc()
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.updateTask(task.copy(syncStatus = SyncStatus.DELETED))
        ReminderScheduler.cancel(appContext, task.id)
        scheduleSyc()
    }

    override suspend fun refreshTasksFromServer() {
        try {
            val tasksDtos = apiService.getTasks()
            val taskEntities = TaskMapper.mapDtoToEntity(tasksDtos)
            taskDao.upsertAll(taskEntities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyReminder(task: Task) {
        if (task.reminderEnabled && !task.isCompleted) {
            val triggerAt = task.dueDate
                .atTime(task.dueHour, task.dueMinute)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            if (triggerAt > System.currentTimeMillis()) {
                ReminderScheduler.schedule(
                    context = appContext,
                    id = task.id,
                    title = task.title,
                    message = "It's time to work on this task.",
                    triggerAtMillis = triggerAt
                )
            } else {
                ReminderScheduler.cancel(appContext, task.id)
            }
        } else {
            ReminderScheduler.cancel(appContext, task.id)
        }
    }

    private fun scheduleSyc() {
        val sychRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .build()
        workManager.enqueue(sychRequest)
    }
}