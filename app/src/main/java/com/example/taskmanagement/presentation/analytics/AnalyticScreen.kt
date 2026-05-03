package com.example.taskmanagement.presentation.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun AnalyticsScreen(
    modifier: Modifier = Modifier,
    viewModel: AnalyticsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    AnalyticsScreen(
        modifier = modifier,
        state = uiState
    )

}

@Composable
private fun AnalyticsScreen(
    modifier: Modifier = Modifier,
    state: AnalyticsUiState
) {
    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator() }
        return
    }
    if (state.categoryData.isEmpty() && state.completedTasksCount == 0) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "No task data for this month")
        }
        return
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(24.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Tasks Completed",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = state.completedTasksCount.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "This Month",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(.7f)
                    )
                }
                Spacer(Modifier.height(16.dp))
                LineGraph(
                    data = state.tasksCompletedPerDay,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

            }

        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Productivity",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Tasks by category",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                state.categoryData.forEach { data ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = data.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(70.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .1f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(data.percentage)
                                    .background(data.color)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${(data.percentage * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                    }
                }
            }

        }
        Spacer(Modifier.height(24.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Completion rate",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row (verticalAlignment = Alignment.Bottom){
                    Text(
                        text = "${(state.completionRate * 100).toInt()}%",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "This Moth",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(.7f)
                    )
                }
                Spacer(Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = {state.completionRate},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer,
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                )
            }
        }

    }

}

@Composable
fun LineGraph(
    modifier: Modifier = Modifier,
    data: List<Float>,
    graphColor: Color = MaterialTheme.colorScheme.primary,
    fillColor: Color = MaterialTheme.colorScheme.primary.copy(.2f)
) {
    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas

        val width = size.width
        val height = size.height

        val maxData = data.maxOrNull() ?: 1f
        val minData = data.minOrNull() ?: 0f

        val range = maxData - minData

        if (range == 0f) return@Canvas

        val points = data.mapIndexed { index, value ->
            val x = (index.toFloat()) / (data.size - 1) * width
            val y = height - ((value - minData) / range) * height
            Offset(x, y)
        }
        if (points.size > 1) {
            val path = Path().apply {
                moveTo(points.first().x, height)
                lineTo(points.first().x, points.first().y)

                for (i in 0 until points.size - 1) {
                    val p1 = points[i]
                    val p2 = points[i + 1]
                    val controlPoint1 = Offset((p1.x + p2.x) / 2, p1.y)
                    val controlPoint2 = Offset((p1.x + p2.x) / 2, p2.y)
                    cubicTo(
                        controlPoint1.x, controlPoint1.y,
                        controlPoint2.x, controlPoint2.y,
                        p2.x, p2.y
                    )
                }
                lineTo(points.last().x, height)
                close()
            }
            drawPath(
                path = path,
                Brush.verticalGradient(listOf(fillColor, Color.Transparent))
            )
            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p1 = points[i]
                    val p2 = points[i + 1]
                    val controlPoint1 = Offset((p1.x + p2.x) / 2, p1.y)
                    val controlPoint2 = Offset((p1.x + p2.x) / 2, p2.y)
                    cubicTo(
                        controlPoint1.x, controlPoint1.y,
                        controlPoint2.x, controlPoint2.y,
                        p2.x, p2.y
                    )

                }

            }
            drawPath(
                path = linePath,
                color = graphColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

        }

    }

}

@Preview(showBackground = true)
@Composable
private fun PrevAnalyticScreen() {
    val previewState = AnalyticsUiState(
        completedTasksCount = 42,
        completionRate = 0.75f,
        tasksCompletedPerDay = listOf(1f, 2f, 0f, 4f, 3f, 1f, 5f, 2f, 3f, 4f, 1f, 6f),
        categoryData = listOf(
            CategoryData("work", 0.8f, Color(0xFF4CAF50)),
            CategoryData("personal", 0.65f, Color(0xFF2196F3)),
            CategoryData("health", 0.3f, Color(0xFF9C27B0))
        ),
        isLoading = false
    )
    AnalyticsScreen(
        state = previewState
    )
}