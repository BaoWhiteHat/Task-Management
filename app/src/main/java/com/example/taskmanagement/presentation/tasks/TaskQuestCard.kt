package com.example.taskmanagement.presentation.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.presentation.ui.theme.TaskTheme
import java.time.format.DateTimeFormatter
import java.util.Locale

private val taskDateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)
private val statusStripWidth = 4.dp
private val statusCheckboxCenterX = 34.dp
private val taskContentStart = 72.dp
private val lightTaskCardHighBorder = Color(0xFFF25545)
private val lightTaskCardMediumBorder = Color(0xFFC47A00)
private val lightTaskCardLowBorder = Color(0xFF5B8F2A)
private val lightTaskCardOverdueBorder = Color(0xFFE53935)
private val lightTaskCardCompletedBorder = Color(0xFF4CAF50)
private val darkTaskCardHighBorder = Color(0xFFFF7B6B)
private val darkTaskCardMediumBorder = Color(0xFFFFB020)
private val darkTaskCardLowBorder = Color(0xFFB7F34A)
private val darkTaskCardOverdueBorder = Color(0xFFFF6B5E)
private val darkTaskCardCompletedBorder = Color(0xFF86D72F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskQuestCard(
    modifier: Modifier = Modifier,
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onFocusClick: (() -> Unit)? = null,
    onEditClick: ((Task) -> Unit)? = null,
    onDeleteClick: ((Task) -> Unit)? = null,
    isDeleteInProgress: Boolean = false,
    deleteErrorMessage: String? = null,
    onDeleteErrorDismissed: () -> Unit = {}
) {
    var showDetails by rememberSaveable(task.id) { mutableStateOf(false) }
    var showDeleteConfirmation by rememberSaveable(task.id) { mutableStateOf(false) }
    val completed = task.isCompleted
    val overdue = isTaskOverdue(task)
    val priorityUi = taskPriorityUi(task.priority)
    val badge = taskCardBadge(task)
    val priorityColor = priorityUi.accentColor
    val accentColor = when {
        overdue -> TaskTheme.colors.overdueAccent
        completed -> TaskTheme.colors.success
        else -> priorityColor
    }
    val defaultSurface = TaskTheme.colors.taskCardSurface.copy(alpha = .96f)
    val surface = when {
        overdue -> TaskTheme.colors.overdueAccent.copy(alpha = .035f).compositeOver(defaultSurface)
        completed -> TaskTheme.colors.taskCardCompletedSurface.copy(alpha = .9f)
        else -> defaultSurface
    }
    val darkTheme = MaterialTheme.colorScheme.background.luminance() < .5f
    val borderColor = when {
        overdue -> if (darkTheme) darkTaskCardOverdueBorder else lightTaskCardOverdueBorder
        completed -> if (darkTheme) darkTaskCardCompletedBorder else lightTaskCardCompletedBorder
        priorityUi.level == TaskPriorityLevel.HIGH -> {
            if (darkTheme) darkTaskCardHighBorder else lightTaskCardHighBorder
        }
        priorityUi.level == TaskPriorityLevel.MEDIUM -> {
            if (darkTheme) darkTaskCardMediumBorder else lightTaskCardMediumBorder
        }
        else -> if (darkTheme) darkTaskCardLowBorder else lightTaskCardLowBorder
    }
    val statusStripColor = when {
        overdue -> TaskTheme.colors.overdueAccent
        completed -> TaskTheme.colors.success
        else -> priorityColor.copy(alpha = .55f)
    }
    val shape = RoundedCornerShape(13.dp)

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = shape,
        color = surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.25.dp, borderColor),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
        ) {
            Box(modifier = Modifier.matchParentSize()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxHeight()
                        .width(statusStripWidth)
                        .background(statusStripColor)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(taskContentStart)
                        .heightIn(min = 44.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Checkbox(
                        checked = completed,
                        onCheckedChange = onCheckedChange,
                        colors = CheckboxDefaults.colors(
                            checkedColor = accentColor,
                            uncheckedColor = accentColor,
                            checkmarkColor = accentContentColor(accentColor)
                        ),
                        modifier = Modifier
                            .offset(x = statusCheckboxCenterX - 12.dp)
                            .size(24.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDetails = true }
                        .padding(vertical = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                letterSpacing = 0.sp
                            ),
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = if (completed) MaterialTheme.colorScheme.onSurface.copy(alpha = .62f)
                            else MaterialTheme.colorScheme.onSurface,
                            textDecoration = if (completed) TextDecoration.LineThrough else TextDecoration.None,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(Modifier.width(8.dp))

                        StateBadge(badge.label, badge.color, badge.backgroundColor)
                    }

                    TaskMetadataLine(
                        task = task,
                        completed = completed,
                        overdue = overdue
                    )
                }
            }
        }
    }

    if (showDetails) {
        TaskDetailsBottomSheet(
            task = task,
            badge = badge,
            canStartFocus = onFocusClick != null && !completed,
            onStartFocus = {
                showDetails = false
                onFocusClick?.invoke()
            },
            onEditTask = {
                showDetails = false
                onEditClick?.invoke(task)
            },
            onRequestDelete = {
                showDeleteConfirmation = true
            },
            isDeleteInProgress = isDeleteInProgress,
            deleteErrorMessage = deleteErrorMessage,
            onDismiss = { showDetails = false }
        )
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleteInProgress) {
                    showDeleteConfirmation = false
                    onDeleteErrorDismissed()
                }
            },
            title = { Text("Delete this task?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("This action cannot be undone.")
                    deleteErrorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (!isDeleteInProgress) {
                            onDeleteClick?.invoke(task)
                        }
                    },
                    enabled = !isDeleteInProgress,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        if (!isDeleteInProgress) {
                            showDeleteConfirmation = false
                            onDeleteErrorDismissed()
                        }
                    },
                    enabled = !isDeleteInProgress
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TaskMetadataLine(
    task: Task,
    completed: Boolean,
    overdue: Boolean
) {
    val metadataColor = when {
        completed -> TaskTheme.colors.completedMetadataText
        overdue -> TaskTheme.colors.overdueAccent.copy(alpha = .88f)
        else -> TaskTheme.colors.taskMetadataText
    }

    if (completed) {
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetadataText(
                text = taskCardStatusLine(task),
                color = metadataColor
            )
        }
    } else {
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetadataText(
                text = taskQuestLine(task),
                color = metadataColor
            )
            MetadataText(
                text = taskCardStatusLine(task),
                color = metadataColor
            )
        }
    }
}

@Composable
private fun MetadataText(
    text: String,
    color: Color
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.sp),
        fontWeight = FontWeight.Medium,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

private fun accentContentColor(accentColor: Color): Color =
    if (accentColor.luminance() > 0.5f) Color(0xFF081105) else Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDetailsBottomSheet(
    task: Task,
    badge: TaskCardBadgeUi,
    canStartFocus: Boolean,
    onStartFocus: () -> Unit,
    onEditTask: () -> Unit,
    onRequestDelete: () -> Unit,
    isDeleteInProgress: Boolean,
    deleteErrorMessage: String?,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val category = taskCategoryOrNull(task.tags)
    val statusText = when {
        task.isCompleted -> "Completed"
        isTaskOverdue(task) -> "Overdue"
        else -> "Incomplete"
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .size(width = 42.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(TaskTheme.colors.subText.copy(alpha = .42f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 620.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleLarge.copy(letterSpacing = 0.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(10.dp))
                StateBadge(badge.label, badge.color, badge.backgroundColor)
            }

            DetailSection(title = "Description") {
                Text(
                    text = task.description.takeIf { it.isNotBlank() } ?: "No description added.",
                    style = MaterialTheme.typography.bodyMedium.copy(letterSpacing = 0.sp),
                    color = if (task.description.isBlank()) {
                        TaskTheme.colors.subText
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            DetailSection(title = "Task details") {
                if (category != null) {
                    CategoryDetailLine(category = category)
                }
                DetailLine(label = "Due date", value = task.dueDate.format(taskDateFormatter))
                DetailLine(label = "Reminder time", value = reminderTimeLabel(task))
                DetailLine(
                    label = "Planned duration",
                    value = "${formatEstimatedDuration(task.estimatedMinutes)} planned"
                )
            }

            DetailSection(title = "Status") {
                DetailLine(label = "Current status", value = statusText)
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .45f))

            deleteErrorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = .55f))
                        .padding(10.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (canStartFocus) {
                    Button(
                        onClick = onStartFocus,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Start Focus")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onEditTask,
                        enabled = !isDeleteInProgress,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text("Edit Task")
                    }
                    OutlinedButton(
                        onClick = onRequestDelete,
                        enabled = !isDeleteInProgress,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete Task")
                    }
                }
                OutlinedButton(
                    onClick = onDismiss,
                    enabled = !isDeleteInProgress,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .72f))
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = .48f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 0.sp),
            fontWeight = FontWeight.SemiBold,
            color = TaskTheme.colors.subText
        )
        content()
    }
}

@Composable
private fun CategoryDetailLine(category: TaskCategory) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Category",
            style = MaterialTheme.typography.bodyMedium.copy(letterSpacing = 0.sp),
            color = TaskTheme.colors.subText,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(12.dp))
        CategoryPill(
            category = category,
            modifier = Modifier.weight(1.1f)
        )
    }
}

@Composable
private fun CategoryPill(
    category: TaskCategory,
    modifier: Modifier = Modifier
) {
    val categoryColors = taskCategoryColors(category)
    Box(modifier = modifier) {
        Text(
            text = category.label,
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
            fontWeight = FontWeight.Bold,
            color = categoryColors.contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .clip(RoundedCornerShape(7.dp))
                .background(categoryColors.containerColor)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun DetailLine(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(letterSpacing = 0.sp),
            color = TaskTheme.colors.subText,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(letterSpacing = 0.sp),
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.1f)
        )
    }
}

@Composable
private fun StateBadge(
    text: String,
    color: Color,
    backgroundColor: Color
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .clip(RoundedCornerShape(7.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}
