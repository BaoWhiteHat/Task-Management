package com.example.taskmanagement.presentation.my_tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.data.local.models.dummyTasks

@Composable
fun MyTasksScreen(
    modifier: Modifier = Modifier,
    viewModel: MyTasksViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    MyTaskScreen(
        state = uiState,
        onTagChange = viewModel::onTagChange,
        onTaskCheckedChange = viewModel::onTaskCheckedChange,
        modifier = modifier
    )

}


@Composable
private fun MyTaskScreen(
    modifier: Modifier = Modifier,
    state: MyTasksUiState,
    onTagChange: (TaskTag) -> Unit,
    onTaskCheckedChange: (Task, Boolean) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        SecondaryTabRow(
            selectedTabIndex = TaskTag.entries.indexOf(state.selectedTag),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            TaskTag.entries.forEach { tag ->
                Tab(
                    selected = state.selectedTag == tag,
                    onClick = { onTagChange(tag) },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .background(
                            color = if (state.selectedTag == tag)
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        ),

                    ) {
                    Text(
                        text = tag.name.lowercase(),
                        color = if (state.selectedTag.name == tag.name) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                state.tasksForSelectedTag,
                key = {it.id}
            ){task ->
                TaskItemComponent(
                    task = task,
                    onCheckedChange = {isCompleted -> onTaskCheckedChange(task,isCompleted)}
                )
            }
        }

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
        onTaskCheckedChange = {_,_ ->},
        onTagChange = {}
    )
}