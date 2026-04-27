package com.example.taskmanagement.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object Utils {
    fun createNotificationChannel(ctx: Context){
        val name = "Task Sync"
        val descriptionText = "Notification for task synchronization status"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel("SYNC_CHANNEL_ID",name,importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }
}