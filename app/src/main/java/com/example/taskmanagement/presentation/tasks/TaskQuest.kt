package com.example.taskmanagement.presentation.tasks

import com.example.taskmanagement.data.local.models.Task
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

fun isTaskOverdue(task: Task, now: LocalDateTime = LocalDateTime.now()): Boolean {
    if (task.isCompleted) return false
    val dueAt = taskDueAt(task) ?: return false
    return now.isAfter(dueAt)
}

fun questTypeLabel(task: Task, now: LocalDateTime = LocalDateTime.now()): String {
    val questType = when (task.priority.trim().lowercase()) {
        "high" -> "Boss Quest"
        "medium" -> "Main Quest"
        else -> "Side Quest"
    }
    return if (isTaskOverdue(task, now)) "Cursed $questType" else questType
}

fun overdueLabel(task: Task, now: LocalDateTime = LocalDateTime.now()): String {
    if (!isTaskOverdue(task, now)) return ""
    val dueAt = taskDueAt(task) ?: return "Overdue"
    val elapsedMinutes = Duration.between(dueAt, now).toMinutes().coerceAtLeast(0)
    val elapsedHours = elapsedMinutes / 60
    val elapsedDays = elapsedHours / 24
    return when {
        elapsedDays > 0 -> "Overdue by ${elapsedDays}d"
        elapsedHours > 0 -> "Overdue by ${elapsedHours}h"
        else -> "Overdue"
    }
}

private fun taskDueAt(task: Task): LocalDateTime? = runCatching {
    LocalDateTime.of(task.dueDate, LocalTime.of(task.dueHour, task.dueMinute))
}.getOrNull()
