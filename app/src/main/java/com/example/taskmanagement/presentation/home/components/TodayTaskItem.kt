package com.example.taskmanagement.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.presentation.ui.theme.TaskTheme
import java.time.LocalDate

@Composable
fun TodayTask(
    modifier: Modifier = Modifier,
    task: Task,
    onCheckedChange: (Boolean) -> Unit
) {
    val priorityColor = when (task.priority.lowercase()) {
        "high" -> TaskTheme.colors.priorityHigh
        "medium" -> TaskTheme.colors.priorityMedium
        else -> TaskTheme.colors.priorityLow
    }
    val priorityBgColor = when (task.priority.lowercase()) {
        "high" -> TaskTheme.colors.priorityHighBg
        "medium" -> TaskTheme.colors.priorityMediumBg
        else -> TaskTheme.colors.priorityLowBg
    }
    val tagColor = when (task.tags.lowercase()) {
        "work" -> TaskTheme.colors.tagWork
        "personal" -> TaskTheme.colors.tagPersonal
        "health" -> TaskTheme.colors.tagHealth
        else -> TaskTheme.colors.tagOther
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .width(4.dp)
                .height(64.dp)
                .background(priorityColor)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.size(22.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    color = if (task.isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .5f)
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = task.tags,
                        style = MaterialTheme.typography.labelSmall,
                        color = tagColor
                    )
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .clip(CircleShape)
                            .background(TaskTheme.colors.subText.copy(alpha = .4f))
                    )
                    Text(
                        text = task.dueDate.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = TaskTheme.colors.subText
                    )
                    if (task.reminderEnabled) {
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .clip(CircleShape)
                                .background(TaskTheme.colors.subText.copy(alpha = .4f))
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Reminder",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = .7f),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = formatReminderTime(task.dueHour, task.dueMinute),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = .85f)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = task.priority.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = priorityColor,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(priorityBgColor)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

private fun formatReminderTime(hour: Int, minute: Int): String {
    val period = if (hour < 12) "AM" else "PM"
    val h12 = if (hour % 12 == 0) 12 else hour % 12
    return "%d:%02d %s".format(h12, minute, period)
}

@Preview
@Composable
private fun TodayTaskPrev() {
    TodayTask(
        task = Task(
            id = 1,
            title = "Completed UI mockup",
            description = "Description",
            remoteId = "1",
            priority = "High",
            reminderEnabled = true,
            dueDate = LocalDate.now(),
            tags = "Work",
            isCompleted = false
        )
    ) {}
}