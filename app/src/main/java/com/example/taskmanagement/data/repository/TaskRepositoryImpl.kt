package com.example.taskmanagement.data.repository

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
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

private val TAG = "TaskRepositoryImpl"

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val apiService: TaskApiService,
    private val workManager: WorkManager
): TaskRepository {
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
    ): Flow<List<Task>> = taskDao.getTasksInDateRange(startDate,endDate)

    override suspend fun insertTask(task: Task) {
        taskDao.insertTask(task.copy(syncStatus = SyncStatus.CREATED))
        scheduleSyc()
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.copy(syncStatus = SyncStatus.UPDATED))
        scheduleSyc()
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.updateTask(task.copy(syncStatus = SyncStatus.DELETED))
        scheduleSyc()
    }

    override suspend fun refreshTasksFromServer() {
        try {
            val tasksDtos = apiService.getTasks()
            val taskEntities = TaskMapper.mapDtoToEntity(tasksDtos)
            taskDao.upsertAll(taskEntities)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun scheduleSyc(){
        val sychRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .build()
        workManager.enqueue(sychRequest)
    }


}