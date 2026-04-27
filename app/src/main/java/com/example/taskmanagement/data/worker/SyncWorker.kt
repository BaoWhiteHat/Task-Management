package com.example.taskmanagement.data.worker

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.taskmanagement.R
import com.example.taskmanagement.data.TaskMapper
import com.example.taskmanagement.data.local.AppDatabase
import com.example.taskmanagement.data.local.models.SyncStatus
import com.example.taskmanagement.di.Graph
import retrofit2.HttpException
import retrofit2.Retrofit
const val SYNC_CHANNEL_ID = "SYNC_CHANNEL_ID"
class SyncWorker(
    private val appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    private val taskDao by lazy { AppDatabase.getDatabase(appContext).taskDao() }
    private val apiService by lazy { Graph.apiService }
    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationId = 1337

    override suspend fun doWork(): Result {
        val dirtyTasks = taskDao.getDirtyTasks()
        if (dirtyTasks.isEmpty()) {
            return Result.success()
        }
        val foregroundInfo = createForegroundInfo("Syncing Tasks...${dirtyTasks.size}")
        setForeground(foregroundInfo)
        try {
            dirtyTasks.forEach { task ->
                val taskDtoItem = TaskMapper.mapEntityToDto(task)
                when (task.syncStatus) {
                    SyncStatus.CREATED -> {
                        val remoteTask = apiService.createTask(taskDtoItem)
                        taskDao.updateTask(
                            task.copy(
                                remoteId = remoteTask.id.toString(),
                                syncStatus = SyncStatus.SYNCED
                            )
                        )
                    }

                    SyncStatus.UPDATED -> {
                        if (task.remoteId.isBlank()) {
                            return@forEach
                        }
                        apiService.updateTask(task.remoteId.toInt(), taskDtoItem)
                        taskDao.updateTask(task.copy(syncStatus = SyncStatus.SYNCED))

                    }

                    SyncStatus.DELETED -> {
                        if (task.remoteId.isBlank()) {
                            taskDao.delete(task)
                            return@forEach
                        }
                        apiService.deleteTask(task.remoteId.toInt())
                        taskDao.delete(task)

                    }

                    SyncStatus.SYNCED -> {
                        /*Do nothing */
                    }
                }
            }
            showNotification("Sync Complete","Tasks synced successfully")
            return Result.success()
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.toString() ?: "unknown error"
            Log.e("SyncWorker", "doWork: errorBody $errorBody ${e.code()}", e)
            if (e.code() in 400..499) {
                showNotification("Sync Failed","Client Error")
                return Result.failure()
            }
            showNotification("Sync Failed","Server Error. Will retry later")
            return Result.retry()
        }
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val notification = createNotification(progress)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }

    private fun createNotification(progress: String): Notification {
        return NotificationCompat.Builder(appContext, SYNC_CHANNEL_ID)
            .setContentTitle("Syncing Tasks")
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    private fun showNotification(title:String,content:String){
        val finalNotification = NotificationCompat.Builder(appContext,SYNC_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        notificationManager.notify(notificationId,finalNotification)
    }

}