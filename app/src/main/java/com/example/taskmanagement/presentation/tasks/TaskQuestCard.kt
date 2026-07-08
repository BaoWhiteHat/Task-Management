package com.example.taskmanagement.presentation.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.presentation.ui.theme.TaskTheme

private val OverdueAccent = Color(0xFFD6A84B)
private val OverdueSurface = Color(0xFF211C12)
private val ClearedSurface = Color(0xFF12240F)

@Composable
fun TaskQuestCard(
    modifier: Modifier = Modifier,
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onFocusClick: (() -> Unit)? = null
) {
    val completed = task.isCompleted
    val overdue = isTaskOverdue(task)
    val questColor = priorityAccentColor(task)
    val accentColor = if (completed) TaskTheme.colors.success else questColor
    val surface = when {
        overdue -> OverdueSurface.copy(alpha = .92f)
        completed -> ClearedSurface.copy(alpha = .9f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .9f)
    }
    val borderColor = when {
        overdue -> OverdueAccent.copy(alpha = .58f)
        completed -> TaskTheme.colors.success.copy(alpha = .44f)
        else -> questColor.copy(alpha = .58f)
    }
    val shape = RoundedCornerShape(14.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (overdue) 4.dp else 2.dp,
                shape = shape,
                ambientColor = borderColor.copy(alpha = if (overdue) .14f else .08f),
                spotColor = borderColor.copy(alpha = if (overdue) .14f else .08f)
            )
            .clip(shape)
            .background(accentColor.copy(alpha = .07f))
            .background(surface)
            .border(1.dp, borderColor, shape)
            .then(if (onFocusClick != null && !completed) Modifier.clickable { onFocusClick() } else Modifier)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = completed,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = accentColor,
                    uncheckedColor = accentColor.copy(alpha = .75f),
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = FontFamily.Default,
                    letterSpacing = 0.sp
                ),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (completed) MaterialTheme.colorScheme.onSurface.copy(alpha = .88f)
                else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            when {
                overdue -> StateBadge("OVERDUE", OverdueAccent, OverdueAccent.copy(alpha = .14f))
                completed -> StateBadge("CLEARED", TaskTheme.colors.success, TaskTheme.colors.success.copy(alpha = .12f))
                else -> PriorityBadge(task)
            }
        }
        when {
            completed -> CompletedContent(task)
            overdue -> OverdueContent(task)
            else -> NormalContent(task)
        }
    }
}

@Composable
private fun NormalContent(task: Task) {
    Text(
        text = "${questTypeLabel(task)} \u00B7 Reminder ${reminderTimeLabel(task)}",
        style = MaterialTheme.typography.labelSmall,
        color = TaskTheme.colors.subText,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    Text(
        text = "Planned ${formatEstimatedDuration(task.estimatedMinutes)}",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = priorityAccentColor(task)
    )
}

@Composable
private fun CompletedContent(task: Task) {
    Text(
        text = questTypeLabel(task),
        style = MaterialTheme.typography.labelSmall,
        color = TaskTheme.colors.subText,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    Text(
        text = "Completed",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = TaskTheme.colors.success
    )
}

@Composable
private fun OverdueContent(task: Task) {
    Text(
        text = "${questTypeLabel(task)} \u00B7 Reminder ${reminderTimeLabel(task)}",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .78f),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    Text(
        text = "Overdue \u00B7 Planned ${formatEstimatedDuration(task.estimatedMinutes)}",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = OverdueAccent
    )
}

@Composable
private fun PriorityBadge(task: Task) {
    when (task.priority.trim().lowercase()) {
        "high" -> StateBadge(
            text = "HIGH",
            color = TaskTheme.colors.priorityHigh,
            backgroundColor = TaskTheme.colors.priorityHighBg
        )
        "medium" -> StateBadge(
            text = "MEDIUM",
            color = TaskTheme.colors.priorityMedium,
            backgroundColor = TaskTheme.colors.priorityMediumBg
        )
        else -> StateBadge(
            text = "LOW",
            color = TaskTheme.colors.priorityLow,
            backgroundColor = TaskTheme.colors.priorityLowBg
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
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 7.dp, vertical = 2.dp)
    )
}

@Composable
private fun priorityAccentColor(task: Task): Color =
    when (task.priority.trim().lowercase()) {
        "high" -> TaskTheme.colors.priorityHigh
        "medium" -> TaskTheme.colors.priorityMedium
        else -> TaskTheme.colors.priorityLow
    }
