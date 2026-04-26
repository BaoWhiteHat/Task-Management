package com.example.taskmanagement.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.data.repository.TaskRepository
import com.example.taskmanagement.di.Graph
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters

enum class CalenderView {
    MONTH,
    WEEK
}

data class CalenderUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val currentMonth: LocalDate = LocalDate.now().withDayOfMonth(1),
    val selectedView: CalenderView = CalenderView.MONTH,
    val tasksForSelectedDate: List<Task> = emptyList(),
    val markedDatesInMonth: Set<LocalDate> = emptySet(),
    val currentWeekMonday: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY)
)

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(
    private val taskRepository: TaskRepository = Graph.repository
) : ViewModel() {
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _currentMonth = MutableStateFlow(LocalDate.now().withDayOfMonth(1))
    private val _selectedView = MutableStateFlow(CalenderView.MONTH)


    private val _tasksForSelectedDate: StateFlow<List<Task>> = _selectedDate
        .flatMapLatest { date -> taskRepository.getTasksForDate(date) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    private val _markedDatesInMonth: StateFlow<Set<LocalDate>> = _currentMonth
        .flatMapLatest { month ->
            val startDate = month.with(TemporalAdjusters.firstDayOfMonth())
            val endDate = month.with(TemporalAdjusters.lastDayOfMonth())
            taskRepository.getDateWithTasks(startDate, endDate)
        }.map { it.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val uiState: StateFlow<CalenderUiState> = combine(
        _selectedDate,
        _currentMonth,
        _selectedView,
        _tasksForSelectedDate,
        _markedDatesInMonth
    ) {selectedDate,currentMonth,view,tasks,markedDates ->
        CalenderUiState(
            selectedDate = selectedDate,
            currentMonth = currentMonth,
            selectedView = view,
            tasksForSelectedDate = tasks,
            markedDatesInMonth = markedDates
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalenderUiState()
    )

    fun onDateSelected(date: LocalDate){_selectedDate.value = date}
    fun onNextMonth(){_currentMonth.value = _currentMonth.value.plusMonths(1)}
    fun onPreviousMonth(){_currentMonth.value = _currentMonth.value.minusMonths(1)}
    fun onNextWeek(){
        val newDate = uiState.value.selectedDate.plusWeeks(1)
        _selectedDate.value = newDate
        _currentMonth.value = newDate.withDayOfMonth(1)
    }
    fun onPreviousWeek(){
        val newDate = uiState.value.selectedDate.minusWeeks(1)
        _selectedDate.value = newDate
        _currentMonth.value = newDate.withDayOfMonth(1)
    }

    fun onTaskCheckedChange(task: Task,isCompleted: Boolean){
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(isCompleted = isCompleted))
        }
    }

    fun onViewChanged(view: CalenderView){_selectedView.value = view}


}


