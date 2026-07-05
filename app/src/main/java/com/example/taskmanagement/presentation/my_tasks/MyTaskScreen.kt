package com.example.taskmanagement.presentation.my_tasks

import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.data.local.models.dummyTasks
import com.example.taskmanagement.presentation.navigation.Screen
import com.example.taskmanagement.presentation.ui.theme.TaskTheme

@Composable
fun MyTasksScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: MyTasksViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    MyTaskScreen(
        state = uiState,
        onTagChange = viewModel::onTagChange,
        onTaskCheckedChange = viewModel::onTaskCheckedChange,
        onFocusClick = { task ->
            navController.navigate(
                "${Screen.Focus.route}?taskId=${task.id}&taskTitle=${Uri.encode(task.title)}" +
                        "&tag=${Uri.encode(task.tags)}&priority=${Uri.encode(task.priority)}"
            )
        },
        modifier = modifier
    )
}

@Composable
private fun MyTaskScreen(
    modifier: Modifier = Modifier,
    state: MyTasksUiState,
    onTagChange: (TaskTag) -> Unit,
    onTaskCheckedChange: (Task, Boolean) -> Unit,
    onFocusClick: (Task) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "My Tasks",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Text(
            text = "${state.tasksForSelectedTag.size} tasks",
            style = MaterialTheme.typography.bodySmall,
            color = TaskTheme.colors.subText,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(TaskTag.entries) { tag ->
                PillTab(
                    text = tag.name.lowercase().replaceFirstChar { it.uppercase() },
                    isSelected = state.selectedTag == tag,
                    onClick = { onTagChange(tag) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (state.tasksForSelectedTag.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No tasks yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = TaskTheme.colors.subText
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Tap + to create a new task",
                        style = MaterialTheme.typography.bodySmall,
                        color = TaskTheme.colors.subText.copy(alpha = .6f)
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    state.tasksForSelectedTag,
                    key = { it.id }
                ) { task ->
                    TaskItemComponent(
                        task = task,
                        onCheckedChange = { isCompleted ->
                            onTaskCheckedChange(task, isCompleted)
                        },
                        onFocusClick = { onFocusClick(task) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PillTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(200),
        label = "pillBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "pillText"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyTaskScreenPrev() {
    MyTaskScreen(
        state = MyTasksUiState(
            tasksForSelectedTag = dummyTasks,
            selectedTag = TaskTag.WORK
        ),
        onTaskCheckedChange = { _, _ -> },
        onTagChange = {}
    )
}
