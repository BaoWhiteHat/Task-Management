package com.example.taskmanagement.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val remoteId: String = "",
    val priority: String,
    val reminderEnabled: Boolean,
    val dueDate: LocalDate,
    val tags: String,
    val isCompleted: Boolean = false,
    val syncStatus: SyncStatus = SyncStatus.CREATED
)
