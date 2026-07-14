package com.example.taskmanagement.presentation.tasks

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.presentation.my_tasks.TaskTag
import com.example.taskmanagement.presentation.ui.theme.TaskTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val taskTimeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)

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

data class TaskCategoryColors(
    val contentColor: Color,
    val containerColor: Color,
    val selectedContainerColor: Color,
    val selectedContentColor: Color
)

enum class TaskCategory(
    val storedValue: String,
    val label: String
) {
    WORK("WORK", "Work"),
    PERSONAL("PERSONAL", "Personal"),
    HEALTH("HEALTH", "Health")
}

fun taskCategoryOrNull(tags: String): TaskCategory? =
    when (tags.trim().uppercase()) {
        TaskCategory.WORK.storedValue -> TaskCategory.WORK
        TaskCategory.PERSONAL.storedValue -> TaskCategory.PERSONAL
        TaskCategory.HEALTH.storedValue -> TaskCategory.HEALTH
        else -> null
    }

@Composable
fun taskCategoryColors(category: TaskCategory): TaskCategoryColors =
    when (category) {
        TaskCategory.WORK -> TaskCategoryColors(
            contentColor = TaskTheme.colors.tagWork,
            containerColor = TaskTheme.colors.tagWorkBg,
            selectedContainerColor = TaskTheme.colors.tagWork,
            selectedContentColor = TaskTheme.colors.selectedTagWorkText
        )
        TaskCategory.PERSONAL -> TaskCategoryColors(
            contentColor = TaskTheme.colors.tagPersonal,
            containerColor = TaskTheme.colors.tagPersonalBg,
            selectedContainerColor = TaskTheme.colors.tagPersonal,
            selectedContentColor = TaskTheme.colors.selectedTagPersonalText
        )
        TaskCategory.HEALTH -> TaskCategoryColors(
            contentColor = TaskTheme.colors.tagHealth,
            containerColor = TaskTheme.colors.tagHealthBg,
            selectedContainerColor = TaskTheme.colors.tagHealth,
            selectedContentColor = TaskTheme.colors.selectedTagHealthText
        )
    }

@Composable
fun taskTagColors(tag: TaskTag): TaskCategoryColors =
    taskCategoryColors(
        when (tag) {
            TaskTag.WORK -> TaskCategory.WORK
            TaskTag.PERSONAL -> TaskCategory.PERSONAL
            TaskTag.HEALTH -> TaskCategory.HEALTH
        }
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
            color = TaskTheme.colors.overdueAccent,
            backgroundColor = TaskTheme.colors.overdueBg
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
