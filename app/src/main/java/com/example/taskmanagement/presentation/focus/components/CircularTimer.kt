package com.example.taskmanagement.presentation.focus.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val GreenBright  = Color(0xFFA8D840)
private val AmberAccent  = Color(0xFFE8A020)
private val TrackColor   = Color(0xFF1D2414)

/**
 * Thin arc ring timer.
 * [progress] = remaining fraction (1.0 = full ring, 0.0 = empty).
 * [isBreak] switches the ring color to amber.
 */
@Composable
fun CircularTimer(
    progress: Float,
    isBreak: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    strokeWidth: Dp = 5.dp
) {
    val targetColor = if (isBreak) AmberAccent else GreenBright
    val ringColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(400),
        label = "ring-color"
    )
    val animProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(500),
        label = "ring-progress"
    )

    Box(
        modifier = modifier
            .size(size)
            .drawBehind {
                val sw = strokeWidth.toPx()
                val radius = (this.size.minDimension - sw) / 2f
                val topLeft = Offset(sw / 2f, sw / 2f)
                val arcSize = Size(radius * 2f, radius * 2f)

                // Background track
                drawArc(
                    color = TrackColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = sw)
                )
                // Foreground arc
                if (animProgress > 0f) {
                    drawArc(
                        color = ringColor,
                        startAngle = -90f,
                        sweepAngle = 360f * animProgress,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = sw, cap = StrokeCap.Round)
                    )
                }
            }
    )
}