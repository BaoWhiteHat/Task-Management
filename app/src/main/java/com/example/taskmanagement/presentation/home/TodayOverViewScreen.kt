package com.example.taskmanagement.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.data.local.models.dummyTasks
import com.example.taskmanagement.presentation.home.components.OverViewCard
import com.example.taskmanagement.presentation.home.components.TodayTask

@Composable
fun TodayOverViewScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiSTate by viewModel.uiState.collectAsState()
    TodayOverViewScreen(
        state = uiSTate,
        onRefresh = viewModel::onRefresh,
        onTaskCheckedChanged = viewModel::onTaskCheckedChange,
        onSortChanged = viewModel::onSortChanged,
        modifier = modifier
    )
}

@Composable
private fun TodayOverViewScreen(
    modifier: Modifier = Modifier,
    state: HomeUIState,
    onRefresh:() -> Unit,
    onTaskCheckedChanged:(Task, Boolean) -> Unit,
    onSortChanged:(SortOrder) -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing = state.sycStatus == SyncStatus.SYNCING
    Column (
        modifier = modifier.fillMaxSize()
    ){
        Row (
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            OverViewCard(
                title = "Tasks Completed",
                count = state.completedCount,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(10.dp))
            OverViewCard(
                title = "Tasks Remaining",
                count = state.remainingCount,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Today's Tasks",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(16.dp))
        PullToRefreshBox(
            state = pullToRefreshState,
            onRefresh = onRefresh,
            isRefreshing = isRefreshing,
        ) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.tasks){task ->
                    TodayTask(
                        task = task,
                        onCheckedChange = {onTaskCheckedChanged(task, it)}
                    )
                }
            }

        }
        AnimatedVisibility(isRefreshing) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Sync,
                    contentDescription = "Sync",
                    tint = MaterialTheme.colorScheme.onSurface.copy(.5f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(text = "Syncing...", color = MaterialTheme.colorScheme.onSurface.copy(.5f))

            }
        }


    }


}

@Preview(showSystemUi = true)
@Composable
private fun TodayOverViewScreenPrev() {
//    val dummyTasks = null
    TodayOverViewScreen(
        state = HomeUIState(
            tasks = dummyTasks
        ),
        onRefresh = {},
        onTaskCheckedChanged = {_,_ ->},
        onSortChanged = {}
    )
}