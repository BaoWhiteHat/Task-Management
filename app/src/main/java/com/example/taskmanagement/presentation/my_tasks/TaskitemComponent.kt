package com.example.taskmanagement.presentation.my_tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import com.example.taskmanagement.data.local.models.dummyTasks
import com.example.taskmanagement.presentation.ui.theme.TaskTheme
import java.time.LocalDate

@Composable
fun TaskItemComponent(
    modifier: Modifier = Modifier,
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onFocusClick: (() -> Unit)? = null
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
        // Priority color bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(72.dp)
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

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    color = if (task.isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .5f)
                    else MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                }
            }

            Spacer(Modifier.width(6.dp))

            // Focus button
            if (onFocusClick != null) {
                Text(
                    text = "Focus",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onFocusClick() }
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
                Spacer(Modifier.width(6.dp))
            }

            // Priority badge
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

@Preview
@Composable
private fun TaskItemCompPrev() {
    TaskItemComponent(
        task = dummyTasks[0],
        onCheckedChange = {}
    )
}