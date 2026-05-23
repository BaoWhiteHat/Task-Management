package com.example.taskmanagement.presentation.analytics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanagement.presentation.ui.theme.TaskTheme

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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "📊", style = MaterialTheme.typography.displayLarge)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "No data this month",
                    style = MaterialTheme.typography.titleMedium,
                    color = TaskTheme.colors.subText
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Complete some tasks to see analytics",
                    style = MaterialTheme.typography.bodySmall,
                    color = TaskTheme.colors.subText.copy(alpha = .6f)
                )
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ── Header ───────────────────────────
        Text(
            text = "Analytics",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "This month's overview",
            style = MaterialTheme.typography.bodySmall,
            color = TaskTheme.colors.subText
        )

        Spacer(Modifier.height(20.dp))

        // ── Stats Row ────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Completed count
            StatCard(
                label = "Completed",
                value = state.completedTasksCount.toString(),
                valueColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            // Completion rate with ring
            CompletionRateCard(
                rate = state.completionRate,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        // ── Line Chart Card ──────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Daily Progress",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Tasks completed per day",
                    style = MaterialTheme.typography.bodySmall,
                    color = TaskTheme.colors.subText
                )
                Spacer(Modifier.height(16.dp))
                LineGraph(
                    data = state.tasksCompletedPerDay,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Category Breakdown ───────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "By Category",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                state.categoryData.forEach { data ->
                    CategoryBar(data = data)
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}


// ─────────────────────────────────────────────
//  Stat Card
// ─────────────────────────────────────────────

@Composable
private fun StatCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TaskTheme.colors.subText
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "this month",
                style = MaterialTheme.typography.labelSmall,
                color = TaskTheme.colors.subText.copy(alpha = .6f)
            )
        }
    }
}


// ─────────────────────────────────────────────
//  Completion Rate Card with Donut
// ─────────────────────────────────────────────

@Composable
private fun CompletionRateCard(
    rate: Float,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedRate by animateFloatAsState(
        targetValue = if (animationPlayed) rate else 0f,
        animationSpec = tween(800),
        label = "rate"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Rate",
                style = MaterialTheme.typography.labelMedium,
                color = TaskTheme.colors.subText
            )
            Spacer(Modifier.height(8.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(72.dp)
            ) {
                Canvas(modifier = Modifier.size(72.dp)) {
                    val strokeWidth = 8.dp.toPx()
                    val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                    // Track
                    drawArc(
                        color = primaryColor.copy(alpha = .15f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    // Progress
                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = animatedRate * 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = "${(animatedRate * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


// ─────────────────────────────────────────────
//  Category Bar
// ─────────────────────────────────────────────

@Composable
private fun CategoryBar(data: CategoryData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(data.color)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = data.name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(70.dp)
        )
        Spacer(Modifier.width(8.dp))
        // Progress bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(data.color.copy(alpha = .15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(data.percentage)
                    .clip(RoundedCornerShape(4.dp))
                    .background(data.color)
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = "${(data.percentage * 100).toInt()}%",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


// ─────────────────────────────────────────────
//  Line Graph (giữ nguyên logic, chỉ dùng theme)
// ─────────────────────────────────────────────

@Composable
fun LineGraph(
    modifier: Modifier = Modifier,
    data: List<Float>,
    graphColor: Color = MaterialTheme.colorScheme.primary,
    fillColor: Color = MaterialTheme.colorScheme.primary.copy(.15f)
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
            val fillPath = Path().apply {
                moveTo(points.first().x, height)
                lineTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p1 = points[i]
                    val p2 = points[i + 1]
                    val cx1 = Offset((p1.x + p2.x) / 2, p1.y)
                    val cx2 = Offset((p1.x + p2.x) / 2, p2.y)
                    cubicTo(cx1.x, cx1.y, cx2.x, cx2.y, p2.x, p2.y)
                }
                lineTo(points.last().x, height)
                close()
            }
            drawPath(fillPath, Brush.verticalGradient(listOf(fillColor, Color.Transparent)))

            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p1 = points[i]
                    val p2 = points[i + 1]
                    val cx1 = Offset((p1.x + p2.x) / 2, p1.y)
                    val cx2 = Offset((p1.x + p2.x) / 2, p2.y)
                    cubicTo(cx1.x, cx1.y, cx2.x, cx2.y, p2.x, p2.y)
                }
            }
            drawPath(linePath, graphColor, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))
        }
    }
}


// ─────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun PrevAnalyticScreen() {
    val previewState = AnalyticsUiState(
        completedTasksCount = 42,
        completionRate = 0.75f,
        tasksCompletedPerDay = listOf(1f, 2f, 0f, 4f, 3f, 1f, 5f, 2f, 3f, 4f, 1f, 6f),
        categoryData = listOf(
            CategoryData("Work", 0.8f, Color(0xFF5B6FF6)),
            CategoryData("Personal", 0.65f, Color(0xFFD4527C)),
            CategoryData("Health", 0.3f, Color(0xFF1D9E75))
        ),
        isLoading = false
    )
    AnalyticsScreen(state = previewState)
}