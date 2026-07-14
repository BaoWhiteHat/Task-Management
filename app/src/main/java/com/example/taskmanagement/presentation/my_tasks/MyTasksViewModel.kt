package com.example.taskmanagement.presentation.my_tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.data.repository.TaskRepository
import com.example.taskmanagement.di.Graph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MyTasksUiState(
    val tasksForSelectedTag: List<Task> = emptyList(),
    val selectedTag: TaskTag = TaskTag.WORK,
    val deletingTaskId: Int? = null,
    val deleteErrorTaskId: Int? = null,
    val deleteErrorMessage: String? = null
)

class MyTasksViewModel(
    private val taskRepository: TaskRepository = Graph.repository
) : ViewModel() {
    private val _selectedTag = MutableStateFlow(TaskTag.WORK)
    private val _deleteState = MutableStateFlow(TaskDeleteState())
    private val _allTasks = taskRepository.getAllTasks()
    val uiState: StateFlow<MyTasksUiState> = combine(
        _allTasks,
        _selectedTag,
        _deleteState
    ) { allTasks, selectedTag, deleteState ->
        val filteredTasks = allTasks.filter {
            it.tags.equals(selectedTag.name, ignoreCase = true)
        }.sortedBy { it.isCompleted }
        MyTasksUiState(
            selectedTag = selectedTag,
            tasksForSelectedTag = filteredTasks,
            deletingTaskId = deleteState.deletingTaskId,
            deleteErrorTaskId = deleteState.errorTaskId,
            deleteErrorMessage = deleteState.errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MyTasksUiState()
    )

    fun onTagChange(tag: TaskTag) {
        _selectedTag.value = tag
    }

    fun onTaskCheckedChange(task: Task, checked: Boolean) {
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(isCompleted = checked))
        }
    }

    fun onDeleteTask(task: Task) {
        if (_deleteState.value.deletingTaskId == task.id) return
        viewModelScope.launch {
            _deleteState.value = TaskDeleteState(deletingTaskId = task.id)
            runCatching { taskRepository.deleteTask(task) }
                .onSuccess {
                    _deleteState.value = TaskDeleteState()
                }
                .onFailure { throwable ->
                    _deleteState.value = TaskDeleteState(
                        errorTaskId = task.id,
                        errorMessage = throwable.message ?: "Task could not be deleted."
                    )
                }
        }
    }

    fun onDeleteErrorDismissed() {
        _deleteState.value = _deleteState.value.copy(
            errorTaskId = null,
            errorMessage = null
        )
    }
}

private data class TaskDeleteState(
    val deletingTaskId: Int? = null,
    val errorTaskId: Int? = null,
    val errorMessage: String? = null
)
