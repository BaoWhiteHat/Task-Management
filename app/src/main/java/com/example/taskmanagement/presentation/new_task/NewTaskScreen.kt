package com.example.taskmanagement.presentation.new_task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanagement.presentation.my_tasks.Priority
import com.example.taskmanagement.presentation.my_tasks.TaskTag
import com.example.taskmanagement.presentation.ui.theme.TaskTheme
import com.example.taskmanagement.presentation.tasks.taskPriorityUi
import com.example.taskmanagement.presentation.tasks.taskTagColors
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

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
        onTaskNameFocusChanged = viewModel::onTaskNameFocusChanged,
        onValidateCurrentStep = viewModel::validateCurrentStep
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
    onTagChange: (TaskTag?) -> Unit,
    onReminderChange: (Boolean) -> Unit,
    onCreateTask: () -> Boolean,
    onTaskNameFocusChanged: (Boolean) -> Unit,
    onValidateCurrentStep: (Int) -> Boolean
) {
    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    val totalSteps = 3

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(22.dp))

        StepIndicator(
            currentStep = currentStep,
            totalSteps = totalSteps
        )

        Spacer(Modifier.height(16.dp))

        val stepTitle = when (currentStep) {
            0 -> "What do you need to do?"
            1 -> "Schedule & Priority"
            else -> "Organize & remind"
        }
        StepHeader(
            stepLabel = "Step ${currentStep + 1} of $totalSteps",
            title = stepTitle,
            labelTitleSpacing = if (currentStep == 1) 4.dp else 6.dp
        )

        Spacer(Modifier.height(if (currentStep == 1) 24.dp else 20.dp))

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
                    showTaskNameError = state.shouldShowTaskNameError,
                    onTitleChange = onTitleChange,
                    onDescriptionChange = onDescriptionChange,
                    onTaskNameFocusChanged = onTaskNameFocusChanged
                )
                1 -> StepDatePriority(
                    dueDate = state.dueDate,
                    dueHour = state.dueHour,
                    dueMinute = state.dueMinute,
                    estimatedMinutes = state.estimatedMinutes,
                    selectedPriority = state.selectedPriority,
                    canContinue = state.canContinueFromStep2,
                    onDueDateChange = onDueDateChange,
                    onTimeChange = onTimeChange,
                    onEstimatedDurationChange = onEstimatedDurationChange,
                    onPriorityChange = onPriorityChange,
                    onBack = { currentStep-- },
                    onContinue = {
                        if (onValidateCurrentStep(currentStep)) {
                            currentStep++
                        }
                    }
                )
                2 -> StepTagReminder(
                    selectedTag = state.selectedTag,
                    isReminderEnabled = state.isReminderEnabled,
                    onTagChange = onTagChange,
                    onReminderChange = onReminderChange
                )
            }
        }

        if (currentStep != 1) {
            Spacer(Modifier.height(16.dp))
            StepNavigationButtons(
                showBack = currentStep > 0,
                primaryLabel = if (currentStep < totalSteps - 1) "Next" else "Create Task",
                primaryEnabled = if (currentStep == 0) state.canContinueFromStep1 else true,
                onBack = { currentStep-- },
                onPrimary = {
                    if (currentStep < totalSteps - 1) {
                        if (onValidateCurrentStep(currentStep)) {
                            currentStep++
                        }
                    } else {
                        if (!onCreateTask() && !state.canContinueFromStep1) {
                            currentStep = 0
                        }
                    }
                }
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StepHeader(
    stepLabel: String,
    title: String,
    labelTitleSpacing: Dp = 6.dp
) {
    Column(verticalArrangement = Arrangement.spacedBy(labelTitleSpacing)) {
        Text(
            text = stepLabel,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun StepNavigationButtons(
    showBack: Boolean,
    primaryLabel: String,
    primaryEnabled: Boolean = true,
    onBack: () -> Unit,
    onPrimary: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (showBack) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(0.4f)
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text("Back")
            }
        }

        Button(
            onClick = onPrimary,
            enabled = primaryEnabled,
            modifier = Modifier
                .weight(if (showBack) 0.6f else 1f)
                .height(52.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .55f)
            )
        ) {
            Text(
                text = primaryLabel,
                fontWeight = FontWeight.SemiBold
            )
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
            val segmentColor = when {
                index < currentStep -> MaterialTheme.colorScheme.primary.copy(alpha = .62f)
                index == currentStep -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(segmentColor)
            )
        }
    }
}

@Composable
private fun StepTitleDescription(
    title: String,
    description: String,
    showTaskNameError: Boolean,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTaskNameFocusChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Task name")
                    Text(
                        text = "*",
                        modifier = Modifier
                            .padding(start = 2.dp)
                            .semantics { contentDescription = "required" },
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            placeholder = { Text("e.g. Finish the report") },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { onTaskNameFocusChanged(it.isFocused) },
            shape = RoundedCornerShape(12.dp),
            isError = showTaskNameError,
            supportingText = {
                if (showTaskNameError) {
                    Text("Task name is required")
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Description")
                    Text(
                        text = "Optional",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = TaskTheme.colors.subText
                    )
                }
            },
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
    canContinue: Boolean,
    onDueDateChange: (LocalDate) -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    onEstimatedDurationChange: (Int, Int) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            onDueDateChange(LocalDate.of(year, month + 1, day))
        },
        dueDate.year,
        dueDate.monthValue - 1,
        dueDate.dayOfMonth
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int -> onTimeChange(hour, minute) },
        dueHour,
        dueMinute,
        false
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 36.dp)
        ) {
            ScheduleSelectionCard(
                label = "Due date",
                value = dueDate.format(dateFormatter),
                icon = {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Pick date",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                onClick = { datePickerDialog.show() }
            )

            Spacer(Modifier.height(16.dp))

            ScheduleSelectionCard(
                label = "Reminder time",
                value = LocalTime.of(dueHour, dueMinute).format(timeFormatter),
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = "Pick time",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                onClick = { timePickerDialog.show() }
            )

            Spacer(Modifier.height(32.dp))

            EstimatedDurationPicker(
                estimatedMinutes = estimatedMinutes,
                onDurationChange = onEstimatedDurationChange
            )

            Spacer(Modifier.height(32.dp))

            PrioritySelector(
                selectedPriority = selectedPriority,
                onPriorityChange = onPriorityChange
            )
        }

        StepNavigationButtons(
            showBack = true,
            primaryLabel = "Continue",
            primaryEnabled = canContinue,
            onBack = onBack,
            onPrimary = onContinue
        )
    }
}

@Composable
private fun ScheduleSelectionCard(
    label: String,
    value: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 92.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = .72f)),
                shape
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = .12f))
                .padding(13.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
    }
}

@Composable
private fun EstimatedDurationPicker(
    estimatedMinutes: Int,
    onDurationChange: (Int, Int) -> Unit
) {
    val safeMinutes = estimatedMinutes.coerceIn(0, 480)
    val shape = RoundedCornerShape(24.dp)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = .72f)),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Planned duration",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatPlannedDuration(safeMinutes),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Slider(
                value = safeMinutes.toFloat(),
                onValueChange = { value ->
                    val snappedMinutes = (value / 30f).roundToInt().times(30).coerceIn(0, 480)
                    onDurationChange(snappedMinutes / 60, snappedMinutes % 60)
                },
                valueRange = 0f..480f,
                steps = 0,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = .28f)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("0h", "4h", "8h").forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PrioritySelector(
    selectedPriority: Priority,
    onPriorityChange: (Priority) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Priority",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Priority.entries.forEach { priority ->
                PriorityButton(
                    priority = priority,
                    selected = selectedPriority == priority,
                    onClick = { onPriorityChange(priority) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PriorityButton(
    priority: Priority,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityUi = taskPriorityUi(priority.name)
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(shape)
            .background(if (selected) priorityUi.badgeBackgroundColor else Color.Transparent)
            .border(
                BorderStroke(
                    width = if (selected) 2.dp else 1.dp,
                    color = priorityUi.accentColor.copy(alpha = if (selected) 1f else .72f)
                ),
                shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = priorityUi.badgeLabel,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = priorityUi.accentColor
        )
    }
}

private fun formatPlannedDuration(totalMinutes: Int): String {
    val safeMinutes = totalMinutes.coerceIn(0, 480)
    val hours = safeMinutes / 60
    val minutes = safeMinutes % 60
    return when {
        safeMinutes == 0 -> "0 min"
        hours == 0 -> "$minutes min"
        else -> "$hours hr ${minutes.toString().padStart(2, '0')} min"
    }
}

@Composable
private fun StepTagReminder(
    selectedTag: TaskTag?,
    isReminderEnabled: Boolean,
    onTagChange: (TaskTag?) -> Unit,
    onReminderChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Category",
                style = MaterialTheme.typography.labelLarge,
                color = TaskTheme.colors.subText
            )
            Text(
                text = "*",
                modifier = Modifier
                    .padding(start = 2.dp)
                    .semantics { contentDescription = "required" },
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer(Modifier.height(10.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TaskTag.entries.forEach { tag ->
                val isSelected = selectedTag == tag
                val tagColors = taskTagColors(tag)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) tagColors.selectedContainerColor
                            else tagColors.containerColor
                        )
                        .clickable { onTagChange(if (isSelected) null else tag) }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = tag.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected)
                            tagColors.selectedContentColor
                        else tagColors.contentColor
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
        state = NewTaskUiState.newTask(),
        onTitleChange = {},
        onDescriptionChange = {},
        onDueDateChange = {},
        onTimeChange = { _, _ -> },
        onEstimatedDurationChange = { _, _ -> },
        onPriorityChange = {},
        onTagChange = {},
        onReminderChange = {},
        onCreateTask = { true },
        onTaskNameFocusChanged = {},
        onValidateCurrentStep = { true }
    )
}
