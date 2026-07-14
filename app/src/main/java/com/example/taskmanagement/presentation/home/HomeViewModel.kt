package com.example.taskmanagement.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.data.repository.TaskRepository
import com.example.taskmanagement.di.Graph
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

enum class SyncStatus {
    IDLE,
    SYNCING,
    SUCCESS
}

enum class SortOrder {
    DEFAULT,
    BY_PRIORITY_DESC,
    BY_TITLE_ASC
}

data class HomeUIState(
    val tasks: List<Task> = emptyList(),
    val completedCount: Int = 0,
    val remainingCount: Int = 0,
    val sycStatus: SyncStatus = SyncStatus.IDLE,
    val sortOrder: SortOrder = SortOrder.DEFAULT,
    val currentTime: LocalDateTime = LocalDateTime.now()
)

class HomeViewModel(
    private val taskRepository: TaskRepository = Graph.repository
): ViewModel() {
    private val _sortOrder = MutableStateFlow(SortOrder.DEFAULT)
    private val _syncStatus = MutableStateFlow(SyncStatus.IDLE)
    private val _currentTime = MutableStateFlow(LocalDateTime.now())
    private val _todayTasksFlow = taskRepository.getAllTasks()

    init {
        viewModelScope.launch {
            while (true) {
                delay(60_000)
                _currentTime.value = LocalDateTime.now()
            }
        }
    }

    val uiState: StateFlow<HomeUIState> = combine(
        _todayTasksFlow,
        _syncStatus,
        _sortOrder,
        _currentTime
    ) {
        tasks, syncStatus, sortOrder, currentTime ->
        val today = currentTime.toLocalDate()
        val actionableTasks = tasks.filter { task ->
            task.dueDate == today || (!task.isCompleted && task.dueDate.isBefore(today))
        }
        val todayTasks = tasks.filter { it.dueDate == today }
        val completedTodayCount = todayTasks.count { it.isCompleted }
        val remainingTodayCount = todayTasks.count { !it.isCompleted }
        HomeUIState(
            tasks = sortedTasks(actionableTasks, sortOrder),
            completedCount = completedTodayCount,
            remainingCount = remainingTodayCount,
            sycStatus = syncStatus,
            sortOrder = sortOrder,
            currentTime = currentTime
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUIState()
    )

    private fun sortedTasks(tasks: List<Task>, sortOrder: SortOrder): List<Task> {
        return when(sortOrder){
            SortOrder.DEFAULT,
            SortOrder.BY_PRIORITY_DESC,
            SortOrder.BY_TITLE_ASC -> tasks.sortedWith(
                compareBy<Task> { it.isCompleted }
                    .thenBy { it.dueDate }
                    .thenBy { it.dueHour }
                    .thenBy { it.dueMinute }
                    .thenBy { it.id }
            )
        }
    }

    fun onRefresh(){
        viewModelScope.launch {
            if (_syncStatus.value == SyncStatus.SYNCING) return@launch
            _syncStatus.value = SyncStatus.SYNCING
            try {
                taskRepository.refreshTasksFromServer()
                _syncStatus.value = SyncStatus.SUCCESS
                delay(3000) // demonstrations purpose
            }catch (e: Exception){
                e.printStackTrace()
            }finally {
                _syncStatus.value = SyncStatus.IDLE
            }
        }
    }

    fun onTaskCheckedChange(task: Task,isCompleted: Boolean){
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(isCompleted = isCompleted))
        }
    }

    fun onSortChanged(sortOrder: SortOrder){
        _sortOrder.value = sortOrder
    }


}
