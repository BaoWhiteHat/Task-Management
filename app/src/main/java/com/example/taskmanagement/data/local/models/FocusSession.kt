package com.example.taskmanagement.data.local.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "focus_sessions",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["taskId"])]
)
data class FocusSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskId: Int? = null,
    val taskTitle: String = "",
    val studyMinutes: Int,
    val breakMinutes: Int,
    val completedDate: LocalDate = LocalDate.now(),
    val completedAtMillis: Long = System.currentTimeMillis()
)
