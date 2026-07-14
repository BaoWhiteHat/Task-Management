package com.example.taskmanagement.presentation.new_task

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
import java.time.LocalTime

data class NewTaskUiState(
    val title: String = "",
    val description: String = "",
    val dueDate: LocalDate,
    val dueHour: Int,
    val dueMinute: Int,
    val estimatedMinutes: Int = 120,
    val selectedPriority: Priority = Priority.MEDIUM,
    val selectedTag: TaskTag? = null,
    val isReminderEnabled: Boolean = false,
    val isTaskSaved: Boolean = false,
    val isEditMode: Boolean = false,
    val isLoadingTask: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val hasTaskNameBeenFocused: Boolean = false,
    val hasTaskNameLostFocus: Boolean = false,
    val taskNameValidationRequested: Boolean = false
) {
    val isTaskNameValid: Boolean
        get() = title.trim().isNotEmpty()

    val canContinueFromStep1: Boolean
        get() = isTaskNameValid

    val isReminderTimeValid: Boolean
        get() = !isReminderEnabled || (dueHour in 0..23 && dueMinute in 0..59)

    val canContinueFromStep2: Boolean
        get() = isReminderTimeValid

    val isCategoryValid: Boolean
        get() = selectedTag != null

    val shouldShowTaskNameError: Boolean
        get() = !isTaskNameValid && (hasTaskNameLostFocus || taskNameValidationRequested)

    companion object {
        fun newTask(): NewTaskUiState {
            val reminderTime = LocalTime.now().withSecond(0).withNano(0)
            return NewTaskUiState(
                dueDate = LocalDate.now(),
                dueHour = reminderTime.hour,
                dueMinute = reminderTime.minute
            )
        }
    }
}

class NewTaskViewModel(
    private val taskRepository: TaskRepository = Graph.repository
) : ViewModel() {
    private companion object {
        const val MIN_ESTIMATED_MINUTES = 30
        const val MAX_ESTIMATED_MINUTES = 480
    }

    private val _uiState = MutableStateFlow(NewTaskUiState.newTask())
    val uiState: StateFlow<NewTaskUiState> = _uiState.asStateFlow()
    private var originalTask: Task? = null
    private var loadedEditTaskId: Int? = null

    fun onTitleChange(title: String) = _uiState.update { it.copy(title = title) }
    fun onDescriptionChange(description: String) = _uiState.update { it.copy(description = description) }
    fun onDueDateChange(dueDate: LocalDate) = _uiState.update { it.copy(dueDate = dueDate) }
    fun onTimeChange(hour: Int, minute: Int) = _uiState.update { it.copy(dueHour = hour, dueMinute = minute) }
    fun onEstimatedDurationChange(hours: Int, minutes: Int) {
        val totalMinutes = (hours * 60 + minutes).coerceIn(0, 480)
        _uiState.update { it.copy(estimatedMinutes = totalMinutes) }
    }
    fun onPriorityChange(priority: Priority) = _uiState.update { it.copy(selectedPriority = priority) }
    fun onTagChange(tag: TaskTag?) = _uiState.update {
        it.copy(
            selectedTag = tag,
            errorMessage = if (tag != null) null else it.errorMessage
        )
    }
    fun onReminderChange(isEnabled: Boolean) = _uiState.update { it.copy(isReminderEnabled = isEnabled) }
    fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }
    fun onTaskSavedHandled() = _uiState.update { it.copy(isTaskSaved = false) }

    fun prepareForCreate() {
        if (!_uiState.value.isEditMode) return
        originalTask = null
        loadedEditTaskId = null
        _uiState.value = NewTaskUiState.newTask()
    }

    fun loadTaskForEdit(taskId: Int) {
        if (loadedEditTaskId == taskId && originalTask?.id == taskId) return
        loadedEditTaskId = taskId
        _uiState.update {
            it.copy(
                isEditMode = true,
                isLoadingTask = true,
                isSaving = false,
                errorMessage = null
            )
        }
        viewModelScope.launch {
            runCatching { taskRepository.getTaskById(taskId) }
                .onSuccess { task ->
                    if (task == null) {
                        originalTask = null
                        _uiState.update {
                            it.copy(
                                isEditMode = true,
                                isLoadingTask = false,
                                errorMessage = "Task could not be found."
                            )
                        }
                    } else {
                        originalTask = task
                        _uiState.update {
                            it.copy(
                                title = task.title,
                                description = task.description,
                                dueDate = task.dueDate,
                                dueHour = task.dueHour,
                                dueMinute = task.dueMinute,
                                estimatedMinutes = task.estimatedMinutes.coerceIn(
                                    MIN_ESTIMATED_MINUTES,
                                    MAX_ESTIMATED_MINUTES
                                ),
                                selectedPriority = priorityFromStoredValue(task.priority),
                                selectedTag = tagFromStoredValue(task.tags),
                                isReminderEnabled = task.reminderEnabled,
                                isTaskSaved = false,
                                isEditMode = true,
                                isLoadingTask = false,
                                isSaving = false,
                                errorMessage = null,
                                hasTaskNameBeenFocused = false,
                                hasTaskNameLostFocus = false,
                                taskNameValidationRequested = false
                            )
                        }
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isEditMode = true,
                            isLoadingTask = false,
                            errorMessage = throwable.message ?: "Task could not be loaded."
                        )
                    }
                }
        }
    }

    fun onTaskNameFocusChanged(isFocused: Boolean) = _uiState.update { state ->
        when {
            isFocused -> state.copy(hasTaskNameBeenFocused = true)
            state.hasTaskNameBeenFocused -> state.copy(hasTaskNameLostFocus = true)
            else -> state
        }
    }

    fun validateCurrentStep(step: Int): Boolean {
        return when (step) {
            0 -> validateStep1()
            1 -> uiState.value.canContinueFromStep2
            else -> validateForSave()
        }
    }

    private fun validateStep1(): Boolean {
        val isValid = uiState.value.canContinueFromStep1
        _uiState.update { it.copy(taskNameValidationRequested = true) }
        return isValid
    }

    private fun validateForSave(): Boolean {
        val state = uiState.value
        if (!state.isTaskNameValid) {
            _uiState.update { it.copy(taskNameValidationRequested = true) }
            return false
        }
        if (!state.canContinueFromStep2) return false
        if (!state.isCategoryValid) {
            _uiState.update { it.copy(errorMessage = "Select a category.") }
            return false
        }
        return true
    }

    fun createTask(): Boolean {
        if (uiState.value.isEditMode) return saveTaskChanges()
        val state = uiState.value
        if (state.isSaving) return false
        val trimmedTitle = state.title.trim()
        if (trimmedTitle.isEmpty()) {
            _uiState.update { it.copy(taskNameValidationRequested = true) }
            return false
        }
        if (!state.canContinueFromStep2) return false
        val selectedTag = state.selectedTag
        if (selectedTag == null) {
            _uiState.update { it.copy(errorMessage = "Select a category.") }
            return false
        }
        val estimatedMinutes = state.estimatedMinutes.coerceIn(
            MIN_ESTIMATED_MINUTES,
            MAX_ESTIMATED_MINUTES
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching {
                val newTask = Task(
                    title = trimmedTitle,
                    description = state.description.trim(),
                    priority = state.selectedPriority.name,
                    reminderEnabled = state.isReminderEnabled,
                    dueDate = state.dueDate,
                    dueHour = state.dueHour,
                    dueMinute = state.dueMinute,
                    estimatedMinutes = estimatedMinutes,
                    tags = selectedTag.name,
                    isCompleted = false
                )
                taskRepository.insertTask(newTask)
            }.onSuccess {
                _uiState.update { it.copy(isTaskSaved = true, isSaving = false) }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "Task could not be created."
                    )
                }
            }
        }
        return true
    }

    private fun saveTaskChanges(): Boolean {
        val state = uiState.value
        if (state.isSaving) return false
        val trimmedTitle = state.title.trim()
        if (trimmedTitle.isEmpty()) {
            _uiState.update { it.copy(taskNameValidationRequested = true) }
            return false
        }
        if (!state.canContinueFromStep2) return false
        val selectedTag = state.selectedTag
        if (selectedTag == null) {
            _uiState.update { it.copy(errorMessage = "Select a category.") }
            return false
        }
        val taskToEdit = originalTask
        if (taskToEdit == null) {
            _uiState.update { it.copy(errorMessage = "Task could not be found.") }
            return false
        }
        val estimatedMinutes = state.estimatedMinutes.coerceIn(
            MIN_ESTIMATED_MINUTES,
            MAX_ESTIMATED_MINUTES
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching {
                val latestTask = taskRepository.getTaskById(taskToEdit.id) ?: taskToEdit
                val updatedTask = latestTask.copy(
                    title = trimmedTitle,
                    description = state.description.trim(),
                    priority = state.selectedPriority.name,
                    reminderEnabled = state.isReminderEnabled,
                    dueDate = state.dueDate,
                    dueHour = state.dueHour,
                    dueMinute = state.dueMinute,
                    estimatedMinutes = estimatedMinutes,
                    tags = selectedTag.name,
                    isCompleted = latestTask.isCompleted
                )
                taskRepository.updateTask(updatedTask)
            }.onSuccess {
                _uiState.update { it.copy(isTaskSaved = true, isSaving = false) }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "Task could not be saved."
                    )
                }
            }
        }
        return true
    }

    private fun priorityFromStoredValue(priority: String): Priority =
        Priority.entries.firstOrNull { it.name.equals(priority.trim(), ignoreCase = true) }
            ?: Priority.MEDIUM

    private fun tagFromStoredValue(tag: String): TaskTag? =
        TaskTag.entries.firstOrNull { it.name.equals(tag.trim(), ignoreCase = true) }
}
