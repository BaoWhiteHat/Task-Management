package com.example.taskmanagement.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.presentation.tasks.isTaskOverdue
import com.example.taskmanagement.presentation.tasks.overdueLabel
import com.example.taskmanagement.presentation.tasks.questTypeLabel
import com.example.taskmanagement.presentation.ui.theme.TaskTheme
import java.time.LocalDate

private val CurseAccent = Color(0xFFFF6E40)
private val CurseSurface = Color(0xFF2B1716)

@Composable
fun TodayTask(
    modifier: Modifier = Modifier,
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onFocusClick: (() -> Unit)? = null
) {
    val overdue = isTaskOverdue(task)
    val priorityColor = when (task.priority.lowercase()) {
        "high" -> TaskTheme.colors.priorityHigh
        "medium" -> TaskTheme.colors.priorityMedium
        else -> TaskTheme.colors.priorityLow
    }
    val tagColor = when (task.tags.lowercase()) {
        "work" -> TaskTheme.colors.tagWork
        "personal" -> TaskTheme.colors.tagPersonal
        "health" -> TaskTheme.colors.tagHealth
        else -> TaskTheme.colors.tagOther
    }
    val shape = RoundedCornerShape(14.dp)

    Row(
        modifier = modifier.fillMaxWidth().clip(shape)
            .background(if (overdue) CurseSurface else MaterialTheme.colorScheme.surfaceVariant)
            .then(if (overdue) Modifier.border(1.dp, CurseAccent.copy(alpha = .85f), shape) else Modifier)
            .then(if (onFocusClick != null && !task.isCompleted) Modifier.clickable { onFocusClick() } else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(if (overdue) 6.dp else 4.dp).height(if (overdue) 128.dp else 104.dp).background(if (overdue) CurseAccent else priorityColor))
        Row(
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = if (overdue) CurseAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (overdue) Icon(Icons.Outlined.WarningAmber, "Cursed quest", tint = CurseAccent, modifier = Modifier.size(16.dp))
                    Text(questTypeLabel(task), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (overdue) CurseAccent else priorityColor)
                    if (overdue) Text(
                        "CURSED", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = CurseAccent,
                        modifier = Modifier.clip(RoundedCornerShape(5.dp)).background(CurseAccent.copy(alpha = .16f)).padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    task.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .5f) else MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(task.tags, style = MaterialTheme.typography.labelSmall, color = tagColor)
                    Box(Modifier.size(3.dp).clip(CircleShape).background(TaskTheme.colors.subText.copy(alpha = .4f)))
                    Text("${task.dueDate}  ${formatReminderTime(task.dueHour, task.dueMinute)}", style = MaterialTheme.typography.labelSmall, color = TaskTheme.colors.subText)
                }
                if (overdue) Text(overdueLabel(task), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = CurseAccent)
                if (onFocusClick != null && !task.isCompleted) {
                    Text(
                        if (overdue) "Purify in Focus" else "Focus on this task",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (overdue) Color(0xFFFFCCBC) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                            .background(if (overdue) CurseAccent.copy(alpha = .18f) else MaterialTheme.colorScheme.primary.copy(alpha = .12f))
                            .clickable { onFocusClick() }.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
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
            title = "Complete UI mockup",
            description = "Description",
            priority = "High",
            reminderEnabled = true,
            dueDate = LocalDate.now(),
            tags = "Work"
        ),
        onCheckedChange = {},
        onFocusClick = {}
    )
}
