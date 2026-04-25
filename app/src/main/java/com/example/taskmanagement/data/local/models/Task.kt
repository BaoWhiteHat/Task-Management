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

val dummyTasks: List<Task> = listOf(
    Task(
        id = 1,
        title = "Complete Project Proposal",
        description = "Draft and finalize the proposal for the new mobile app project, including features and timeline.",
        priority = "High",
        reminderEnabled = true,
        dueDate = LocalDate.of(2025, 10, 24), // Using LocalDate.of(year, month, day)
        tags = "Work",
        isCompleted = true
    ),
    Task(
        id = 2,
        title = "Grocery Shopping",
        description = "Buy milk, eggs, bread, fruits, and vegetables for the week.",
        priority = "Medium",
        reminderEnabled = true,
        dueDate = LocalDate.of(2025, 10, 24),
        tags = "Personal",

        ),
    Task(
        id = 3,
        title = "Call Mom",
        description = "Catch up with Mom and ask about her week.",
        priority = "Medium",
        reminderEnabled = false,
        dueDate = LocalDate.of(2025, 10, 25),
        tags = "Personal",
        isCompleted = true
    ),
    Task(
        id = 4,
        title = "Exercise - Morning Run",
        description = "Go for a 30-minute run in the park.",
        priority = "Low",
        reminderEnabled = true,
        dueDate = LocalDate.of(2025, 10, 25),
        tags = "Health",
        isCompleted = true
    ),

    )