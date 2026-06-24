package com.example.taskmanagement.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.data.local.models.dummyTasks
import com.example.taskmanagement.presentation.focus.GreenBright
import com.example.taskmanagement.presentation.home.components.OverViewCard
import com.example.taskmanagement.presentation.home.components.TodayTask
import com.example.taskmanagement.presentation.ui.theme.TaskTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.clickable
import com.example.taskmanagement.presentation.rewards.DailyRewardsButton

@Composable
fun TodayOverViewScreen(
    modifier: Modifier = Modifier,
    onStartFocus: () -> Unit = {},
    onOpenHub: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    TodayOverViewScreen(
        state = uiState,
        onRefresh = viewModel::onRefresh,
        onTaskCheckedChanged = viewModel::onTaskCheckedChange,
        onSortChanged = viewModel::onSortChanged,
        onStartFocus = onStartFocus,
        onOpenHub = onOpenHub,
        modifier = modifier
    )
}

@Composable
private fun TodayOverViewScreen(
    modifier: Modifier = Modifier,
    state: HomeUIState,
    onRefresh: () -> Unit,
    onTaskCheckedChanged: (Task, Boolean) -> Unit,
    onSortChanged: (SortOrder) -> Unit,
    onStartFocus: () -> Unit = {},
    onOpenHub: () -> Unit = {}
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing = state.sycStatus == SyncStatus.SYNCING

    val totalTasks = state.completedCount + state.remainingCount
    val progress = if (totalTasks > 0) state.completedCount.toFloat() / totalTasks else 0f

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        GreetingSection(onStartFocus = onStartFocus, onOpenHub = onOpenHub)

        Spacer(Modifier.height(16.dp))

        ProgressHeroCard(
            completedCount = state.completedCount,
            totalCount = totalTasks,
            progress = progress
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OverViewCard(
                title = "Completed",
                count = state.completedCount,
                modifier = Modifier.weight(1f),
                isSuccess = true
            )
            OverViewCard(
                title = "Remaining",
                count = state.remainingCount,
                modifier = Modifier.weight(1f),
                isSuccess = false
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Today's Tasks",
            style = MaterialTheme.typography.titleMedium,
            color = TaskTheme.colors.subText,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(8.dp))

        PullToRefreshBox(
            state = pullToRefreshState,
            onRefresh = onRefresh,
            isRefreshing = isRefreshing,
            modifier = Modifier.weight(1f)
        ) {
            if (state.tasks.isEmpty()) {
                EmptyTaskState(
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.tasks, key = { it.id }) { task ->
                        TodayTask(
                            task = task,
                            onCheckedChange = { onTaskCheckedChanged(task, it) }
                        )
                    }
                }
            }
        }

        AnimatedVisibility(isRefreshing) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Sync,
                    contentDescription = "Sync",
                    tint = TaskTheme.colors.subText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Syncing...",
                    style = MaterialTheme.typography.bodySmall,
                    color = TaskTheme.colors.subText
                )
            }
        }
    }
}

@Composable
private fun GreetingSection(
    modifier: Modifier = Modifier,
    onStartFocus: () -> Unit = {},
    onOpenHub: () -> Unit = {}
) {
    val currentHour = remember { java.time.LocalTime.now().hour }
    val greeting = when {
        currentHour < 12 -> "Good Morning"
        currentHour < 18 -> "Good Afternoon"
        else -> "Good Evening"
    }
    val today = remember {
        LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE, dd/MM", Locale.ENGLISH)
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = today,
                style = MaterialTheme.typography.bodySmall,
                color = TaskTheme.colors.subText
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "$greeting!",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DailyRewardsButton()
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onOpenHub() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "B",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ProgressHeroCard(
    completedCount: Int,
    totalCount: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) progress else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "progress"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    val accent = GreenBright

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Today's Progress",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = .7f)
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "$completedCount",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " / $totalCount",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = .6f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "tasks completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = .7f)
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(72.dp)
            ) {
                Canvas(modifier = Modifier.size(72.dp)) {
                    val strokeWidth = 8.dp.toPx()
                    val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                    drawArc(
                        color = accent.copy(alpha = .25f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    drawArc(
                        color = accent,
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EmptyTaskState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎉",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "No tasks for today",
            style = MaterialTheme.typography.titleMedium,
            color = TaskTheme.colors.subText
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Add a new task to get started!",
            style = MaterialTheme.typography.bodySmall,
            color = TaskTheme.colors.subText.copy(alpha = .7f)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun TodayOverViewScreenPrev() {
    TodayOverViewScreen(
        state = HomeUIState(
            tasks = dummyTasks,
            completedCount = 3,
            remainingCount = 1
        ),
        onRefresh = {},
        onTaskCheckedChanged = { _, _ -> },
        onSortChanged = {}
    )
}