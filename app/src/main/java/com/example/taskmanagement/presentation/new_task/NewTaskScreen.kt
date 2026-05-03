package com.example.taskmanagement.presentation.new_task

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.widget.DatePicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanagement.presentation.my_tasks.Priority
import com.example.taskmanagement.presentation.my_tasks.TaskTag
import java.time.LocalDate

@Composable
fun NewTaskScreen(
    modifier: Modifier = Modifier,
    viewModel: NewTaskViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.isTaskSaved) {
        if (uiState.isTaskSaved) {
            onNavigateBack()
            viewModel.onTaskSavedHandled()
        }
    }
    NewTaskScreen(
        modifier = modifier,
        state = uiState,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onDueDateChange = viewModel::onDueDateChange,
        onPriorityChange = viewModel::onPriorityChange,
        onTagChange = viewModel::onTagChange,
        onCreateTask = viewModel::createTask,
        onReminderChange = viewModel::onReminderChange,
    )

}

@Composable
private fun NewTaskScreen(
    modifier: Modifier = Modifier,
    state: NewTaskUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDueDateChange: (LocalDate) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onTagChange: (TaskTag) -> Unit,
    onReminderChange: (Boolean) -> Unit,
    onCreateTask: () -> Unit,
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMoth: Int ->
            onDueDateChange(LocalDate.of(year, month + 1, dayOfMoth))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(state.errorMessage != null) {
            state.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        OutlinedTextField(
            value = state.title,
            onValueChange = onTitleChange,
            label = { Text(text = "Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = state.description,
            onValueChange = onDescriptionChange,
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            maxLines = 5
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = state.dueDate.toString(),
            onValueChange = {},
            label = { Text("Due Date") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() },
            trailingIcon = {
                IconButton(
                    onClick = {
                        datePickerDialog.show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Selected Date"
                    )
                }
            }
        )
        Spacer(Modifier.height(16.dp))
        TaskItemContainer {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Priority", modifier = Modifier.align(Alignment.Start))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Priority.entries.forEach { priority ->
                        TaskPriority(
                            priority = priority,
                            isSelected = state.selectedPriority == priority,
                            onPriorityClick = { onPriorityChange(priority) }
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        TaskItemContainer {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tags", modifier = Modifier.align(Alignment.Start)
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TaskTag.entries.forEach { tag ->
                        TaskTagItem(
                            tag = tag,
                            isSelected = state.selectedTag == tag,
                            onTagClick = { onTagChange(tag) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        TaskItemContainer {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Reminder", style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = state.isReminderEnabled,
                    onCheckedChange = onReminderChange
                )
            }
        }
        Spacer(Modifier.weight(1f))

        Button(
            onClick = onCreateTask,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "Create Task")
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun NewTaskScreenPrev() {
    NewTaskScreen(
        state = NewTaskUiState(),
        onTitleChange = {},
        onDescriptionChange = {},
        onDueDateChange = {},
        onPriorityChange = {},
        onTagChange = {},
        onReminderChange = {},
        onCreateTask = {}
    )
}