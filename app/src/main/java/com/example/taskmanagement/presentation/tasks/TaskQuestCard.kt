package com.example.taskmanagement.presentation.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskQuestCard(
    modifier: Modifier = Modifier,
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onFocusClick: (() -> Unit)? = null
) {
    var showDetails by rememberSaveable(task.id) { mutableStateOf(false) }
    val completed = task.isCompleted
    val overdue = isTaskOverdue(task)
    val priorityUi = taskPriorityUi(task.priority)
    val badge = taskCardBadge(task)
    val priorityColor = priorityUi.accentColor
    val accentColor = when {
        overdue -> TaskOverdueAccent
        completed -> TaskTheme.colors.success
        else -> priorityColor
    }
    val surface = when {
        overdue -> TaskTheme.colors.taskCardOverdueSurface.copy(alpha = .92f)
        completed -> TaskTheme.colors.taskCardCompletedSurface.copy(alpha = .9f)
        else -> TaskTheme.colors.taskCardSurface.copy(alpha = .96f)
    }
    val borderColor = when {
        overdue -> TaskTheme.colors.taskCardOverdueBorder
        completed -> TaskTheme.colors.taskCardCompletedBorder
        else -> TaskTheme.colors.taskCardBorder
    }
    val shape = RoundedCornerShape(13.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = shape,
                ambientColor = borderColor.copy(alpha = .08f),
                spotColor = borderColor.copy(alpha = .08f)
            )
            .clip(shape)
            .background(surface)
            .border(1.dp, borderColor, shape)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 34.dp, height = 38.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(accentColor.copy(alpha = if (completed) .12f else .08f)),
            contentAlignment = Alignment.Center
        ) {
            Checkbox(
                checked = completed,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = accentColor,
                    uncheckedColor = accentColor.copy(alpha = .75f),
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.width(10.dp))

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
                    maxLines = 1,
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

    if (showDetails) {
        TaskDetailsBottomSheet(
            task = task,
            badge = badge,
            canStartFocus = onFocusClick != null && !completed,
            onStartFocus = {
                showDetails = false
                onFocusClick?.invoke()
            },
            onDismiss = { showDetails = false }
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
        completed -> TaskTheme.colors.success.copy(alpha = .88f)
        overdue -> TaskOverdueAccent.copy(alpha = .88f)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDetailsBottomSheet(
    task: Task,
    badge: TaskCardBadgeUi,
    canStartFocus: Boolean,
    onStartFocus: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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

            DetailSection(title = "Schedule") {
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Close")
                }
                if (canStartFocus) {
                    Button(
                        onClick = onStartFocus,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Start Focus")
                    }
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
