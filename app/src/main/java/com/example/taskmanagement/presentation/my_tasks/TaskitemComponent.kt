package com.example.taskmanagement.presentation.my_tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.data.local.models.dummyTasks

@Composable
fun TaskItemComponent(
    modifier: Modifier = Modifier,
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
) {
    val priority = Priority.entries.find { it.name == task.priority } ?: Priority.LOW
    Card(
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(.5f)
                ),
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column (
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ){
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(12.dp),
                        tint = priority.color,
                        imageVector = Icons.Filled.Circle,
                        contentDescription = "Priority"
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${priority.name} Priority",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Text(
                text = task.dueDate.toString(),
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        }
    }

}

@Preview
@Composable
private fun TaskItemCompPrev() {
    TaskItemComponent(
        task = dummyTasks[0]
    ) { }
}
