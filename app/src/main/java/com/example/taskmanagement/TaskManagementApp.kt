package com.example.taskmanagement

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.taskmanagement.data.worker.SYNC_CHANNEL_ID
import com.example.taskmanagement.di.Graph

class TaskManagementApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val syncChannel = NotificationChannel(
                SYNC_CHANNEL_ID,
                "Task Sync",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for background task sync"
            }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(syncChannel)
        }
    }
}