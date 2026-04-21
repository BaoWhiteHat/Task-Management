package com.example.taskmanagement.data

import com.example.taskmanagement.data.local.models.SyncStatus
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.data.remote.models.TaskDto
import com.example.taskmanagement.data.remote.models.TaskDtoItem
import java.time.LocalDate

object TaskMapper {
    fun mapDtoToEntity(dto: com.example.taskmanagement.data.remote.models.TaskDto): List<com.example.taskmanagement.data.local.models.Task> {
        return dto.map { dtoItem ->
            val safeDueDate = try {
                LocalDate.parse(dtoItem.dueDate)
            } catch (e: Exception) {
                LocalDate.now()
            }

            Task(
                remoteId = dtoItem.id.toString(),
                title = dtoItem.title,
                description = dtoItem.description,
                priority = dtoItem.priority,
                reminderEnabled = dtoItem.reminderEnabled,
                dueDate = safeDueDate,
                tags = dtoItem.tags,
                isCompleted = dtoItem.isCompleted,
                syncStatus = SyncStatus.SYNCED
            )
        }
    }

    fun mapEntityToDto(entity: Task): TaskDtoItem {
        return TaskDtoItem(
            id = if (entity.remoteId.isNotBlank()) entity.remoteId.toInt() else 0,
            title = entity.title,
            description = entity.description,
            priority = entity.priority,
            reminderEnabled = entity.reminderEnabled,
            dueDate = entity.dueDate.toString(), // Convert LocalDate to a standard "YYYY-MM-DD" string
            tags = entity.tags,
            isCompleted = entity.isCompleted
        )
    }
}