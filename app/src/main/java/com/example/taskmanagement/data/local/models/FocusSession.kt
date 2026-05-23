package com.example.taskmanagement.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskTitle: String = "",
    val studyMinutes: Int,
    val breakMinutes: Int,
    val completedDate: LocalDate = LocalDate.now(),
    val completedAtMillis: Long = System.currentTimeMillis()
)