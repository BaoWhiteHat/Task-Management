package com.example.taskmanagement.presentation.tasks

import com.example.taskmanagement.data.local.models.Task
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val taskTimeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)

fun taskReminderDateTime(task: Task): LocalDateTime? = runCatching {
    LocalDateTime.of(task.dueDate, LocalTime.of(task.dueHour, task.dueMinute))
}.getOrNull()

fun isTaskOverdue(task: Task): Boolean {
    val isOverdue = !task.isCompleted && task.dueDate.isBefore(LocalDate.now())
    return isOverdue
}

fun questTypeLabel(task: Task): String {
    val questType = when (task.priority.trim().lowercase()) {
        "high" -> "Boss Quest"
        "medium" -> "Main Quest"
        else -> "Side Quest"
    }
    return when {
        task.isCompleted -> "$questType Cleared"
        isTaskOverdue(task) -> "Overdue $questType"
        else -> questType
    }
}

fun formatEstimatedDuration(totalMinutes: Int): String {
    val safeMinutes = totalMinutes.coerceIn(30, 480)
    val hours = safeMinutes / 60
    val minutes = safeMinutes % 60
    return when {
        hours == 0 -> "${minutes}m"
        minutes == 0 -> "${hours}h"
        else -> "${hours}h ${minutes}m"
    }
}

fun reminderTimeLabel(task: Task): String =
    taskReminderDateTime(task)?.format(taskTimeFormatter) ?: "Unknown"
