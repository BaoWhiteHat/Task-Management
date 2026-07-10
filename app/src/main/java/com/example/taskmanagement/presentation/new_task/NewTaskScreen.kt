package com.example.taskmanagement.presentation.new_task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.widget.DatePicker
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanagement.presentation.my_tasks.Priority
import com.example.taskmanagement.presentation.my_tasks.TaskTag
import com.example.taskmanagement.presentation.ui.theme.TaskTheme
import com.example.taskmanagement.presentation.tasks.formatEstimatedDuration
import com.example.taskmanagement.presentation.tasks.taskPriorityUi
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
        onTimeChange = viewModel::onTimeChange,
        onEstimatedDurationChange = viewModel::onEstimatedDurationChange,
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
    onTimeChange: (Int, Int) -> Unit,
    onEstimatedDurationChange: (Int, Int) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onTagChange: (TaskTag) -> Unit,
    onReminderChange: (Boolean) -> Unit,
    onCreateTask: () -> Unit,
) {
    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    val totalSteps = 3

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AnimatedVisibility(state.errorMessage != null) {
            state.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(12.dp)
                )
                Spacer(Modifier.height(12.dp))
            }
        }

        StepIndicator(
            currentStep = currentStep,
            totalSteps = totalSteps
        )

        Spacer(Modifier.height(24.dp))

        val stepTitle = when (currentStep) {
            0 -> "What do you need to do?"
            1 -> "When & how important?"
            else -> "Organize & remind"
        }
        Text(
            text = stepTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(20.dp))

        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                } else {
                    slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                }
            },
            modifier = Modifier.weight(1f),
            label = "stepContent"
        ) { step ->
            when (step) {
                0 -> StepTitleDescription(
                    title = state.title,
                    description = state.description,
                    onTitleChange = onTitleChange,
                    onDescriptionChange = onDescriptionChange
                )
                1 -> StepDatePriority(
                    dueDate = state.dueDate,
                    dueHour = state.dueHour,
                    dueMinute = state.dueMinute,
                    estimatedMinutes = state.estimatedMinutes,
                    selectedPriority = state.selectedPriority,
                    onDueDateChange = onDueDateChange,
                    onTimeChange = onTimeChange,
                    onEstimatedDurationChange = onEstimatedDurationChange,
                    onPriorityChange = onPriorityChange
                )
                2 -> StepTagReminder(
                    selectedTag = state.selectedTag,
                    isReminderEnabled = state.isReminderEnabled,
                    onTagChange = onTagChange,
                    onReminderChange = onReminderChange
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentStep > 0) {
                OutlinedButton(
                    onClick = { currentStep-- },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder(true)
                ) {
                    Text("Back")
                }
            }

            Button(
                onClick = {
                    if (currentStep < totalSteps - 1) {
                        currentStep++
                    } else {
                        onCreateTask()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (currentStep < totalSteps - 1) "Next" else "Create Task",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(totalSteps) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (index <= currentStep) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            )
        }
    }
}

@Composable
private fun StepTitleDescription(
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Title",
            style = MaterialTheme.typography.labelLarge,
            color = TaskTheme.colors.subText
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = { Text("e.g. Finish the report") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Description",
            style = MaterialTheme.typography.labelLarge,
            color = TaskTheme.colors.subText
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            placeholder = { Text("Add some details...") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 140.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            maxLines = 6
        )
    }
}

@Composable
private fun StepDatePriority(
    dueDate: LocalDate,
    dueHour: Int,
    dueMinute: Int,
    estimatedMinutes: Int,
    selectedPriority: Priority,
    onDueDateChange: (LocalDate) -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    onEstimatedDurationChange: (Int, Int) -> Unit,
    onPriorityChange: (Priority) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            onDueDateChange(LocalDate.of(year, month + 1, day))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int -> onTimeChange(hour, minute) },
        dueHour,
        dueMinute,
        false
    )

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Due Date",
            style = MaterialTheme.typography.labelLarge,
            color = TaskTheme.colors.subText
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = dueDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Pick date",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Reminder Time",
            style = MaterialTheme.typography.labelLarge,
            color = TaskTheme.colors.subText
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = LocalTime.of(dueHour, dueMinute).format(DateTimeFormatter.ofPattern("hh:mm a")),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { timePickerDialog.show() },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            trailingIcon = {
                IconButton(onClick = { timePickerDialog.show() }) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = "Pick time",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        Spacer(Modifier.height(24.dp))

        EstimatedDurationPicker(
            estimatedMinutes = estimatedMinutes,
            onDurationChange = onEstimatedDurationChange
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Priority",
            style = MaterialTheme.typography.labelLarge,
            color = TaskTheme.colors.subText
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Priority.entries.forEach { priority ->
                val isSelected = selectedPriority == priority
                val priorityUi = taskPriorityUi(priority.name)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) priorityUi.accentColor else priorityUi.badgeBackgroundColor)
                        .clickable { onPriorityChange(priority) }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = priorityUi.badgeLabel,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimary
                        else priorityUi.accentColor
                    )
                }
            }
        }
    }
}

@Composable
private fun EstimatedDurationPicker(
    estimatedMinutes: Int,
    onDurationChange: (Int, Int) -> Unit
) {
    val selectedHours = estimatedMinutes / 60
    val selectedMinutes = estimatedMinutes % 60

    Text(
        text = "Estimated Duration",
        style = MaterialTheme.typography.labelLarge,
        color = TaskTheme.colors.subText
    )
    Spacer(Modifier.height(6.dp))
    Text(
        text = formatEstimatedDuration(estimatedMinutes),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(Modifier.height(10.dp))
    Text("Hours", style = MaterialTheme.typography.labelSmall, color = TaskTheme.colors.subText)
    Spacer(Modifier.height(6.dp))
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        (0..8).forEach { hours ->
            val enabled = !(hours == 0 && selectedMinutes == 0) &&
                    !(hours == 8 && selectedMinutes == 30)
            DurationOption(
                text = "${hours}h",
                selected = selectedHours == hours,
                enabled = enabled,
                onClick = { onDurationChange(hours, selectedMinutes) }
            )
        }
    }
    Spacer(Modifier.height(10.dp))
    Text("Minutes", style = MaterialTheme.typography.labelSmall, color = TaskTheme.colors.subText)
    Spacer(Modifier.height(6.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(0, 30).forEach { minutes ->
            val enabled = !(selectedHours == 0 && minutes == 0) &&
                    !(selectedHours == 8 && minutes == 30)
            DurationOption(
                text = "${minutes}m",
                selected = selectedMinutes == minutes,
                enabled = enabled,
                onClick = { onDurationChange(selectedHours, minutes) }
            )
        }
    }
}

@Composable
private fun DurationOption(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val accent = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(9.dp))
            .background(if (selected) accent else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = when {
                selected -> MaterialTheme.colorScheme.onPrimary
                enabled -> MaterialTheme.colorScheme.onSurfaceVariant
                else -> TaskTheme.colors.subText.copy(alpha = .35f)
            }
        )
    }
}

@Composable
private fun StepTagReminder(
    selectedTag: TaskTag?,
    isReminderEnabled: Boolean,
    onTagChange: (TaskTag) -> Unit,
    onReminderChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Category",
            style = MaterialTheme.typography.labelLarge,
            color = TaskTheme.colors.subText
        )
        Spacer(Modifier.height(10.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TaskTag.entries.forEach { tag ->
                val isSelected = selectedTag == tag
                val tagColor = when (tag) {
                    TaskTag.WORK -> TaskTheme.colors.tagWork
                    TaskTag.PERSONAL -> TaskTheme.colors.tagPersonal
                    TaskTag.HEALTH -> TaskTheme.colors.tagHealth
                }
                val tagBgColor = when (tag) {
                    TaskTag.WORK -> TaskTheme.colors.tagWorkBg
                    TaskTag.PERSONAL -> TaskTheme.colors.tagPersonalBg
                    TaskTag.HEALTH -> TaskTheme.colors.tagHealthBg
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) tagColor else tagBgColor)
                        .clickable { onTagChange(tag) }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = tag.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimary
                        else tagColor
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Reminder",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Notify me at the planned start time",
                    style = MaterialTheme.typography.bodySmall,
                    color = TaskTheme.colors.subText
                )
            }
            Switch(
                checked = isReminderEnabled,
                onCheckedChange = onReminderChange,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

//  Preview
@Preview(showBackground = true)
@Composable
private fun NewTaskScreenPrev() {
    NewTaskScreen(
        state = NewTaskUiState(),
        onTitleChange = {},
        onDescriptionChange = {},
        onDueDateChange = {},
        onTimeChange = { _, _ -> },
        onEstimatedDurationChange = { _, _ -> },
        onPriorityChange = {},
        onTagChange = {},
        onReminderChange = {},
        onCreateTask = {}
    )
}
