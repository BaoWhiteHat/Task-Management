package com.example.taskmanagement.presentation.analytics

import android.R.attr.tag
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.data.repository.TaskRepository
import com.example.taskmanagement.di.Graph
import com.example.taskmanagement.presentation.my_tasks.TaskTag
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

data class CategoryData(
    val name: String,
    val percentage: Float,
    val color: Color,
)

data class AnalyticsUiState(
    val completedTasksCount: Int = 0,
    val completionRate: Float = 0f,
    val tasksCompletedPerDay: List<Float> = emptyList(),
    val categoryData: List<CategoryData> = emptyList(),
    val isLoading: Boolean = true
)

class AnalyticsViewModel(
    private val taskRepository: TaskRepository = Graph.repository
) : ViewModel() {
    private val today = LocalDate.now()
    private val firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth())
    private val lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth())

    val uiState: StateFlow<AnalyticsUiState> =
        taskRepository.getTasksInDateRange(firstDayOfMonth, lastDayOfMonth)
            .map { tasksInMonth ->
                if (tasksInMonth.isEmpty())
                    return@map AnalyticsUiState(isLoading = false)
                val totalTasks = tasksInMonth.size
                val completedTasks = tasksInMonth.filter { it.isCompleted }
                val completedCount = completedTasks.size
                val completionRate = if (totalTasks > 0) completedCount.toFloat() / totalTasks else 0f
                val tasksCompletedPerDay =
                    calculateTasksPerDay(completedTasks, firstDayOfMonth, lastDayOfMonth)
                val categoryData = calculateCategoryData(tasksInMonth)

                AnalyticsUiState(
                    completedTasksCount = completedCount,
                    completionRate = completionRate,
                    tasksCompletedPerDay = tasksCompletedPerDay,
                    categoryData = categoryData,
                    isLoading = false
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AnalyticsUiState()
            )

    private fun calculateTasksPerDay(
        completedTasks: List<Task>,
        start: LocalDate,
        end: LocalDate
    ): List<Float> {
        val daysInMonth = (0..end.dayOfYear - start.dayOfYear).map {
            start.plusDays(it.toLong())
        }
        val tasksGroupedByDate = completedTasks.groupBy { it.dueDate }
        return daysInMonth.map { date ->
            tasksGroupedByDate[date]?.size?.toFloat() ?: 0f
        }
    }

    private fun calculateCategoryData(
        tasks: List<Task>
    ): List<CategoryData> {
        val tasksByCategory = tasks.groupBy { it.tags }
        return tasksByCategory.mapNotNull { (tag, tasksInCategory) ->
            try {
                val taskTag = TaskTag.valueOf(tag.uppercase())
                val completedInCategory = tasksInCategory.count { it.isCompleted }
                val percentage = if (tasksInCategory.isNotEmpty())
                    completedInCategory.toFloat() / tasksInCategory.size
                else 0f
                val color = when (taskTag) {
                    TaskTag.WORK -> Color(0xFF4CAF50)
                    TaskTag.PERSONAL -> Color(0xFF2196F3)
                    TaskTag.HEALTH -> Color(0xFF9C27B0)

                }
                CategoryData(
                    name = tag.lowercase(),
                    percentage = percentage,
                    color = color
                )
            }catch (e: IllegalStateException){
                null
            }
        }
            .sortedByDescending { it.percentage }
    }
}