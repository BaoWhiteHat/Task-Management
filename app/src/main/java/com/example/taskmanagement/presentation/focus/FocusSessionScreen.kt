package com.example.taskmanagement.presentation.focus

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.taskmanagement.R
import com.example.taskmanagement.presentation.ui.theme.TaskTheme
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset

@Composable
fun FocusSessionScreen(
    state: FocusUiState,
    timeText: String,
    onBackToSetup: () -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onConfirmReset: () -> Unit,
    onDismissPenalty: () -> Unit,
    onSkip: () -> Unit
) {
    var isImmersive by rememberSaveable { mutableStateOf(false) }

    if (isImmersive) {
        ImmersiveView(
            state = state,
            timeText = timeText,
            onBack = { isImmersive = false }
        )
    } else {
        ControlView(
            state = state,
            timeText = timeText,
            onBackToSetup = onBackToSetup,
            onStart = onStart,
            onPause = onPause,
            onReset = onReset,
            onSkip = onSkip,
            onEnterImmersive = { isImmersive = true }
        )
    }

    // Penalty warning dialog
    if (state.showPenaltyWarning) {
        PenaltyWarningDialog(
            onConfirm = onConfirmReset,
            onDismiss = onDismissPenalty
        )
    }
}


// ═════════════════════════════════════════════
//  IMMERSIVE VIEW — Full screen game mode
// ═════════════════════════════════════════════

@OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun ImmersiveView(
    state: FocusUiState,
    timeText: String,
    onBack: () -> Unit
) {
    val bgRes = if (state.isBreak) R.drawable.focus_bg_day else R.drawable.focus_bg_night

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark vignette overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Black.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.5f)
                        )
                    )
                )
        )

        // Back button top-left
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable(onClick = onBack)
                .align(Alignment.TopStart),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Phase label top-right
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .align(Alignment.TopEnd)
        ) {
            Text(
                text = if (state.isBreak) "Break" else "Focus",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                letterSpacing = 1.5.sp
            )
        }

        // Center: timer + status
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Big timer text with shadow
            Text(
                text = timeText,
                fontSize = 84.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                letterSpacing = (-2).sp
            )

            // Progress bar
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(state.elapsedProgress)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.85f))
                )
            }

            Text(
                text = "${(state.elapsedProgress * 100).toInt()}% elapsed",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
                letterSpacing = 2.sp
            )
        }

        // Status text bottom
        Text(
            text = if (state.isBreak) "RESTING"
            else if (state.isRunning) "FOCUSING"
            else "PAUSED",
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.5f),
            letterSpacing = 4.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
        )

        // Now playing bottom (if sound)
        state.selectedSoundId?.let { soundId ->
            val profile = state.gameProfile
            if (profile != null && profile.hasSound(soundId)) {
                val emoji = when (soundId) {
                    "rain" -> "\uD83C\uDF27"
                    "forest" -> "\uD83C\uDF32"
                    "fireplace" -> "\uD83D\uDD25"
                    "ocean" -> "\uD83C\uDF0A"
                    else -> "\uD83C\uDFB5"
                }
                val soundName = ambientSounds.find { it.id == soundId }?.name ?: soundId

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = emoji, fontSize = 14.sp)
                    Text(
                        text = soundName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

//  CONTROL VIEW — Buttons and info
@Composable
private fun ControlView(
    state: FocusUiState,
    timeText: String,
    onBackToSetup: () -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onSkip: () -> Unit,
    onEnterImmersive: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(onClick = onBackToSetup),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (state.isBreak) "Break Time" else "Focus Session",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (state.isBreak) "take a short rest"
                    else "stay focused until break time",
                    style = MaterialTheme.typography.bodySmall,
                    color = TaskTheme.colors.subText
                )
            }
            // Phase badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (state.isBreak) TaskTheme.colors.accentColor.copy(alpha = .15f)
                        else MaterialTheme.colorScheme.primary.copy(alpha = .15f)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (state.isBreak) "Break" else "Focus",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (state.isBreak) TaskTheme.colors.accentColor
                    else MaterialTheme.colorScheme.primary
                )
            }
        }

        // Tree preview with timer ring — tap to enter immersive
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = onEnterImmersive),
            contentAlignment = Alignment.Center
        ) {
            val treeRes = getTreeResource(state.elapsedProgress)
            val ringColor = if (state.isBreak) TaskTheme.colors.accentColor
            else MaterialTheme.colorScheme.primary
            val trackColor = MaterialTheme.colorScheme.outline.copy(alpha = .15f)
            val animProgress by animateFloatAsState(
                targetValue = state.remainingProgress,
                animationSpec = tween(500),
                label = "ring"
            )

            // Timer ring
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .drawBehind {
                        val sw = 6.dp.toPx()
                        val radius = (size.minDimension - sw) / 2f
                        val topLeft = Offset(sw / 2f, sw / 2f)
                        val arcSize = Size(radius * 2, radius * 2)

                        drawArc(
                            color = trackColor,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = sw)
                        )
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
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = treeRes),
                    contentDescription = "Tree preview",
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Text(
                text = "Tap to enter focus world",
                style = MaterialTheme.typography.labelSmall,
                color = TaskTheme.colors.subText,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            )
        }

        // Timer
        Text(
            text = timeText,
            fontSize = 48.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = (-2).sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "${(state.elapsedProgress * 100).toInt()}% elapsed",
            style = MaterialTheme.typography.labelSmall,
            color = TaskTheme.colors.subText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Study/Break chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PhaseChip("STUDY", "${state.selectedPreset.studyMinutes}m", !state.isBreak, Modifier.weight(1f))
            PhaseChip("BREAK", "${state.selectedPreset.breakMinutes}m", state.isBreak, Modifier.weight(1f))
        }

        // Now playing
        state.selectedSoundId?.let { soundId ->
            val profile = state.gameProfile
            if (profile != null && profile.hasSound(soundId)) {
                NowPlayingBar(soundId = soundId, isRunning = state.isRunning)
            }
        }

        Spacer(Modifier.weight(1f))

        // Controls
        // Main button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (state.isRunning) MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.primary
                )
                .then(
                    if (state.isRunning) Modifier.border(
                        1.dp, TaskTheme.colors.accentColor, RoundedCornerShape(14.dp)
                    ) else Modifier
                )
                .clickable(onClick = if (state.isRunning) onPause else onStart)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (state.isRunning) "Pause"
                else if (state.elapsedProgress > 0f) "Resume"
                else "Start Session",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (state.isRunning) TaskTheme.colors.accentColor
                else MaterialTheme.colorScheme.onPrimary
            )
        }

        // Secondary buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(onClick = onReset)
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Reset", style = MaterialTheme.typography.labelMedium, color = TaskTheme.colors.subText)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(onClick = onSkip)
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Skip", style = MaterialTheme.typography.labelMedium, color = TaskTheme.colors.subText)
            }
        }
    }
}

//  WOODEN TIMER BOARD
@Composable
private fun WoodenTimerBoard(
    timeText: String,
    elapsed: Float,
    isBreak: Boolean
) {
    Box(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF5A3E1A),
                        Color(0xFF3D2A10),
                        Color(0xFF5A3E1A)
                    )
                )
            )
            .border(
                width = 2.dp,
                color = Color(0xFF7A5625),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = timeText,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF5E6C8),
                letterSpacing = 2.sp
            )
            Text(
                text = "${(elapsed * 100).toInt()}% elapsed",
                fontSize = 12.sp,
                color = Color(0xFFBFA67A),
                letterSpacing = 1.sp
            )
        }
    }
}

//  PHASE CHIP
@Composable
private fun PhaseChip(
    label: String,
    value: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline.copy(alpha = .3f),
        animationSpec = tween(300), label = "chip-border"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(300), label = "chip-bg"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(0.5.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TaskTheme.colors.subText,
                letterSpacing = 1.5.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isActive) MaterialTheme.colorScheme.primary
                else TaskTheme.colors.subText
            )
        }
    }
}

//  NOW PLAYING BAR
@Composable
private fun NowPlayingBar(soundId: String, isRunning: Boolean) {
    val soundName = ambientSounds.find { it.id == soundId }?.name ?: soundId
    val emoji = when (soundId) {
        "rain" -> "\uD83C\uDF27"
        "forest" -> "\uD83C\uDF32"
        "fireplace" -> "\uD83D\uDD25"
        "ocean" -> "\uD83C\uDF0A"
        else -> "\uD83C\uDFB5"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = emoji, fontSize = 16.sp)
            Text(
                text = soundName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (isRunning) "Playing" else "Paused",
                style = MaterialTheme.typography.labelSmall,
                color = TaskTheme.colors.subText
            )
        }
    }
}

//  PENALTY WARNING DIALOG
@Composable
private fun PenaltyWarningDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Your tree will wither!", fontWeight = FontWeight.Bold)
        },
        text = {
            Text("Quitting before 50% will cost you 15 XP, 5 coins, and reset your streak. Are you sure?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Quit anyway", color = TaskTheme.colors.priorityHigh)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Keep going")
            }
        }
    )
}

//  TREE HELPERS
private fun getTreeStage(progress: Float): Int {
    return when {
        progress < 0.12f -> 1
        progress < 0.25f -> 2
        progress < 0.37f -> 3
        progress < 0.50f -> 4
        progress < 0.62f -> 5
        progress < 0.75f -> 6
        progress < 0.87f -> 7
        else -> 8
    }
}

private fun getTreeResource(progress: Float): Int {
    return when (getTreeStage(progress)) {
        1 -> R.drawable.tree_stage_1
        2 -> R.drawable.tree_stage_2
        3 -> R.drawable.tree_stage_3
        4 -> R.drawable.tree_stage_4
        5 -> R.drawable.tree_stage_5
        6 -> R.drawable.tree_stage_6
        7 -> R.drawable.tree_stage_7
        else -> R.drawable.tree_stage_8
    }
}