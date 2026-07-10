package com.example.taskmanagement.presentation.tasks

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.presentation.ui.theme.TaskTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val taskTimeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)

val TaskOverdueAccent = Color(0xFFFF7A6B)

enum class TaskPriorityLevel(
    val normalizedValue: String,
    val badgeLabel: String,
    val questLabel: String
) {
    HIGH("high", "High", "Boss Quest"),
    MEDIUM("medium", "Medium", "Main Quest"),
    LOW("low", "Low", "Side Quest")
}

data class TaskPriorityUi(
    val level: TaskPriorityLevel,
    val accentColor: Color,
    val badgeBackgroundColor: Color
) {
    val normalizedValue: String = level.normalizedValue
    val badgeLabel: String = level.badgeLabel
    val questLabel: String = level.questLabel
}

data class TaskCardBadgeUi(
    val label: String,
    val color: Color,
    val backgroundColor: Color
)

fun normalizedTaskPriority(priority: String): TaskPriorityLevel =
    when (priority.trim().lowercase()) {
        "high" -> TaskPriorityLevel.HIGH
        "medium" -> TaskPriorityLevel.MEDIUM
        else -> TaskPriorityLevel.LOW
    }

@Composable
fun taskPriorityUi(priority: String): TaskPriorityUi =
    when (val level = normalizedTaskPriority(priority)) {
        TaskPriorityLevel.HIGH -> TaskPriorityUi(
            level = level,
            accentColor = TaskTheme.colors.priorityHigh,
            badgeBackgroundColor = TaskTheme.colors.priorityHighBg
        )
        TaskPriorityLevel.MEDIUM -> TaskPriorityUi(
            level = level,
            accentColor = TaskTheme.colors.priorityMedium,
            badgeBackgroundColor = TaskTheme.colors.priorityMediumBg
        )
        TaskPriorityLevel.LOW -> TaskPriorityUi(
            level = level,
            accentColor = TaskTheme.colors.priorityLow,
            badgeBackgroundColor = TaskTheme.colors.priorityLowBg
        )
    }

fun taskReminderDateTime(task: Task): LocalDateTime? = runCatching {
    LocalDateTime.of(task.dueDate, LocalTime.of(task.dueHour, task.dueMinute))
}.getOrNull()

fun isTaskOverdue(task: Task): Boolean {
    val isOverdue = !task.isCompleted && task.dueDate.isBefore(LocalDate.now())
    return isOverdue
}

@Composable
fun taskCardBadge(task: Task): TaskCardBadgeUi {
    val priorityUi = taskPriorityUi(task.priority)
    return when {
        isTaskOverdue(task) -> TaskCardBadgeUi(
            label = "Overdue",
            color = TaskOverdueAccent,
            backgroundColor = TaskOverdueAccent.copy(alpha = .16f)
        )
        task.isCompleted -> TaskCardBadgeUi(
            label = "Cleared",
            color = TaskTheme.colors.successBadgeText,
            backgroundColor = TaskTheme.colors.successBadgeBackground
        )
        else -> TaskCardBadgeUi(
            label = priorityUi.badgeLabel,
            color = priorityUi.accentColor,
            backgroundColor = priorityUi.badgeBackgroundColor
        )
    }
}

fun taskBaseQuestLabel(task: Task): String =
    normalizedTaskPriority(task.priority).questLabel

fun taskQuestLine(task: Task): String {
    return when {
        task.isCompleted -> "Completed"
        else -> reminderTimeLabel(task)
    }
}

fun taskCardStatusLine(task: Task): String =
    when {
        task.isCompleted -> "Completed"
        else -> "${formatEstimatedDuration(task.estimatedMinutes)} planned"
    }

fun questTypeLabel(task: Task): String {
    val questType = taskBaseQuestLabel(task)
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
