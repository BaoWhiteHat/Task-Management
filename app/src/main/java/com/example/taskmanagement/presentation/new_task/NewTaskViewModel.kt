package com.example.taskmanagement.presentation.new_task

import android.R.attr.description
import android.R.attr.priority
import android.R.attr.tag
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.data.repository.TaskRepository
import com.example.taskmanagement.di.Graph
import com.example.taskmanagement.presentation.my_tasks.Priority
import com.example.taskmanagement.presentation.my_tasks.TaskTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class NewTaskUiState(
    val title: String = "",
    val description:String = "",
    val dueDate: LocalDate = LocalDate.now(),
    val selectedPriority: Priority = Priority.LOW,
    val selectedTag: TaskTag? = TaskTag.WORK,
    val isReminderEnabled: Boolean = false,
    val isTaskSaved: Boolean = false,
    val errorMessage: String? = null
)

class NewTaskViewModel (
    private val taskRepository: TaskRepository = Graph.repository
): ViewModel(){
    private val _uiState = MutableStateFlow(NewTaskUiState())
    val uiState: StateFlow<NewTaskUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String) = _uiState.update { it.copy(title = title) }
    fun onDescriptionChange(description: String) = _uiState.update { it.copy(description = description) }
    fun onDueDateChange(dueDate: LocalDate) = _uiState.update { it.copy(dueDate = dueDate) }
    fun onPriorityChange(priority: Priority) = _uiState.update { it.copy(selectedPriority = priority) }
    fun onTagChange(tag: TaskTag) = _uiState.update { it.copy(selectedTag = tag) }
    fun onReminderChange(isEnabled: Boolean) = _uiState.update { it.copy(isReminderEnabled = isEnabled) }
    fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }
    fun onTaskSavedHandled() = _uiState.update { it.copy(isTaskSaved = false) }

    fun createTask(){
        val state = uiState.value
        if (state.title.isBlank()){
            _uiState.update { it.copy(errorMessage = "Title is required") }
            return
        }
        if (state.selectedTag == null){
            _uiState.update { it.copy(errorMessage = "Tag is required") }
            return
        }

        viewModelScope.launch {
            val newTask = Task(
                title = state.title,
                description = state.description.trim(),
                priority = state.selectedPriority.name,
                reminderEnabled = state.isReminderEnabled,
                dueDate = state.dueDate,
                tags = state.selectedTag.name,
                isCompleted = false
            )
            taskRepository.insertTask(newTask)
            _uiState.update { it.copy(isTaskSaved = true) }
        }
    }

}