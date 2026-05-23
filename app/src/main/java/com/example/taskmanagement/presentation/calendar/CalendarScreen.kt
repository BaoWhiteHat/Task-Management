package com.example.taskmanagement.presentation.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.data.local.models.dummyTasks
import com.example.taskmanagement.presentation.my_tasks.TaskItemComponent
import com.example.taskmanagement.presentation.ui.theme.TaskTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    CalendarScreen(
        modifier = modifier,
        state = uiState,
        onDateSelected = viewModel::onDateSelected,
        onNextWeek = viewModel::onNextWeek,
        onNextMonth = viewModel::onNextMonth,
        onTaskCheckedChange = viewModel::onTaskCheckedChange,
        onViewChanged = viewModel::onViewChanged,
        onPreviousWeek = viewModel::onPreviousWeek,
        onPreviousMonth = viewModel::onPreviousMonth,
    )
}

@Composable
private fun CalendarScreen(
    modifier: Modifier = Modifier,
    state: CalenderUiState,
    onDateSelected: (LocalDate) -> Unit,
    onNextMonth: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextWeek: () -> Unit,
    onPreviousWeek: () -> Unit,
    onViewChanged: (CalenderView) -> Unit,
    onTaskCheckedChange: (Task, Boolean) -> Unit
) {
    val today = remember { LocalDate.now() }
    val isMonthView = state.selectedView == CalenderView.MONTH

    Column(modifier = modifier.fillMaxSize()) {

        // ── Header ───────────────────────────
        Text(
            text = "Calendar",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // ── View toggle ──────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            SegmentedButton(
                modifier = Modifier.fillMaxWidth(0.55f),
                selectedItem = if (isMonthView) "Month" else "Week",
                items = listOf("Month", "Week"),
                onItemClick = {
                    onViewChanged(if (it == "Month") CalenderView.MONTH else CalenderView.WEEK)
                }
            )
        }

        Spacer(Modifier.height(12.dp))

        // ── Month/Week navigator ─────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { if (isMonthView) onPreviousMonth() else onPreviousWeek() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = state.currentMonth.format(
                    DateTimeFormatter.ofPattern(
                        if (isMonthView) "MMMM yyyy" else "dd MMM yyyy",
                        Locale.ENGLISH
                    )
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            IconButton(onClick = { if (isMonthView) onNextMonth() else onNextWeek() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Calendar grid ────────────────────
        if (isMonthView) {
            CalendarGrid(
                selectedDate = state.selectedDate,
                currentMonth = state.currentMonth,
                markedDates = state.markedDatesInMonth,
                onDateSelected = onDateSelected,
                today = today
            )
        } else {
            WeeklyCalendarGrid(
                currentWeekStartDate = state.currentWeekMonday,
                selectedDate = state.selectedDate,
                markedDates = state.markedDatesInMonth,
                onDateSelected = onDateSelected,
                today = today
            )
        }

        Spacer(Modifier.height(20.dp))

        // ── Selected date header ─────────────
        Text(
            text = state.selectedDate.format(
                DateTimeFormatter.ofPattern("MMMM d", Locale.ENGLISH)
            ),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            text = "${state.tasksForSelectedDate.size} tasks",
            style = MaterialTheme.typography.bodySmall,
            color = TaskTheme.colors.subText,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(10.dp))

        // ── Task list ────────────────────────
        if (state.tasksForSelectedDate.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tasks for this day",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TaskTheme.colors.subText
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.tasksForSelectedDate, key = { it.id }) { task ->
                    TaskItemComponent(
                        task = task,
                        onCheckedChange = { isChecked ->
                            onTaskCheckedChange(task, isChecked)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarScreenPrev() {
    CalendarScreen(
        state = CalenderUiState(tasksForSelectedDate = dummyTasks),
        onDateSelected = {},
        onNextMonth = {},
        onPreviousMonth = {},
        onNextWeek = {},
        onPreviousWeek = {},
        onViewChanged = {},
        onTaskCheckedChange = { _, _ -> }
    )
}