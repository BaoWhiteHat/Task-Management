package com.example.taskmanagement.presentation.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

@Composable
fun FocusSessionScreen(
    state: FocusUiState,
    timeText: String,
    onBackToSetup: () -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onSkip: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp, bottom = 100.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SessionHeader(
                isBreak = state.isBreak,
                onBackToSetup = onBackToSetup
            )

            TimerSection(
                state = state,
                timeText = timeText
            )

            TreeBreakdownRow(
                state = state
            )

            ControlsSection(
                isRunning = state.isRunning,
                hasStarted = state.timeLeft < state.selectedPreset.studySeconds || state.isBreak,
                onStart = onStart,
                onPause = onPause,
                onReset = onReset,
                onSkip = onSkip
            )
        }
    }
}

@Composable
private fun SessionHeader(
    isBreak: Boolean,
    onBackToSetup: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Surface1)
                .border(0.5.dp, BorderSubtle, CircleShape)
                .clickable(onClick = onBackToSetup),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Back to setup",
                tint = TextPrimary
            )
        }

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                text = if (isBreak) "Break Time" else "Focus Session",
                fontSize = 26.sp,
                fontWeight = FontWeight.Light,
                color = TextPrimary,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = if (isBreak) "take a short rest" else "stay focused until break time",
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun TimerSection(state: FocusUiState, timeText: String) {
    val ringColor = if (state.isBreak) AmberAccent else GreenBright
    val elapsed = state.elapsedProgress

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.phaseTitle.lowercase(),
                    fontSize = 11.sp,
                    color = TextMuted,
                    letterSpacing = 1.5.sp
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (state.isBreak) Color(0x26E8A020) else GreenDark
                ) {
                    Text(
                        text = if (state.isBreak) "Break" else "Focus",
                        fontSize = 11.sp,
                        color = if (state.isBreak) AmberAccent else GreenBright,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                RingTimer(
                    progress = state.remainingProgress,
                    color = ringColor,
                    size = 80.dp
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = timeText,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Light,
                        color = TextPrimary,
                        letterSpacing = (-2).sp,
                        lineHeight = 44.sp
                    )

                    Text(
                        text = "${(elapsed * 100).toInt()}% elapsed",
                        fontSize = 11.sp,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Surface2)
            ) {
                val animProg by animateFloatAsState(
                    targetValue = elapsed,
                    animationSpec = tween(500),
                    label = "progress"
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animProg.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(10.dp))
                        .background(ringColor)
                )
            }
        }
    }
}

@Composable
private fun RingTimer(
    progress: Float,
    color: Color,
    size: Dp
) {
    val animProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500),
        label = "ring"
    )

    Box(
        modifier = Modifier
            .size(size)
            .drawBehind {
                val stroke = 5.dp.toPx()
                val radius = (this.size.minDimension - stroke) / 2f
                val topLeft = Offset(stroke / 2f, stroke / 2f)
                val arcSize = Size(radius * 2, radius * 2)

                drawArc(
                    color = Surface2,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke)
                )

                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * animProgress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
    )
}

@Composable
private fun TreeBreakdownRow(state: FocusUiState) {
    val preset = state.selectedPreset

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1.2f)
                .clip(RoundedCornerShape(20.dp))
                .background(Surface1)
                .border(0.5.dp, BorderSubtle, RoundedCornerShape(20.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompactTree(progress = state.elapsedProgress)

                Text(
                    text = if (state.isBreak) {
                        "resting..."
                    } else if (state.isRunning) {
                        "growing!"
                    } else {
                        "waiting"
                    },
                    fontSize = 11.sp,
                    color = if (state.isRunning && !state.isBreak) GreenBright else TextMuted,
                    letterSpacing = 1.sp
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BreakdownChip(
                label = "STUDY",
                value = "${preset.studyMinutes}m",
                isActive = !state.isBreak
            )

            BreakdownChip(
                label = "BREAK",
                value = "${preset.breakMinutes}m",
                isActive = state.isBreak
            )
        }
    }
}

@Composable
private fun BreakdownChip(
    label: String,
    value: String,
    isActive: Boolean
) {
    val borderColor by animateColorAsState(
        targetValue = if (isActive) GreenMid else BorderSubtle,
        animationSpec = tween(300),
        label = "chip-border"
    )

    val bgColor by animateColorAsState(
        targetValue = if (isActive) Surface2 else Surface1,
        animationSpec = tween(300),
        label = "chip-bg"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(0.5.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = label,
                fontSize = 9.sp,
                color = TextMuted,
                letterSpacing = 1.5.sp
            )

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Light,
                color = if (isActive) GreenBright else TextMuted,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun CompactTree(progress: Float) {
    val safe = progress.coerceIn(0f, 1f)

    val canopySize by animateFloatAsState(
        targetValue = 36f + safe * 28f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 80f),
        label = "canopy"
    )

    val trunkHeight by animateFloatAsState(
        targetValue = 16f + safe * 18f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 100f),
        label = "trunk"
    )

    val canopyColor by animateColorAsState(
        targetValue = if (safe < 0.3f) GreenDark else if (safe < 0.7f) GreenMid else GreenBright,
        animationSpec = tween(600),
        label = "canopy-color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(canopySize.dp)
                .background(canopyColor, CircleShape)
        )

        Spacer(Modifier.height(3.dp))

        Box(
            modifier = Modifier
                .width(9.dp)
                .height(trunkHeight.dp)
                .background(TrunkBrown, RoundedCornerShape(50))
        )

        Spacer(Modifier.height(3.dp))

        Box(
            modifier = Modifier
                .width(44.dp)
                .height(3.dp)
                .background(GreenDark, RoundedCornerShape(10.dp))
        )
    }
}

@Composable
private fun ControlsSection(
    isRunning: Boolean,
    hasStarted: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onSkip: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (isRunning) Surface2 else GreenBright)
                .then(
                    if (isRunning) {
                        Modifier.border(0.5.dp, AmberAccent, RoundedCornerShape(14.dp))
                    } else {
                        Modifier
                    }
                )
                .clickable(onClick = if (isRunning) onPause else onStart)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isRunning) "Pause" else if (hasStarted) "Resume" else "Start Session",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (isRunning) AmberAccent else BgDeep,
                letterSpacing = 0.5.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SecondaryButton(
                text = "↺  Reset",
                onClick = onReset,
                modifier = Modifier.weight(1f)
            )

            SecondaryButton(
                text = "Skip  →",
                onClick = onSkip,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = TextMuted
        )
    }
}