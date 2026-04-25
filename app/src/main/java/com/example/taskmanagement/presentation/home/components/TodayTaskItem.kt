package com.example.taskmanagement.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskmanagement.data.local.models.Task
import java.time.LocalDate

@Composable
fun TodayTask(
    modifier: Modifier = Modifier,
    task: Task,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ){
        Row (
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .5f)
                    ),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column{
                    Text (
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = task.dueDate.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f)
                    )

                }
            }
        }
    }
}

@Preview
@Composable
private fun TodayTaskPrev(){
    TodayTask(
        task = Task(
            id = 1,
            title = "Task 1",
            description = "Description 1",
            remoteId = "1",
            priority = "High",
            reminderEnabled = true,
            dueDate = LocalDate.now(),
            tags = "Tag 1",
            isCompleted = false
        )
    ){}
}