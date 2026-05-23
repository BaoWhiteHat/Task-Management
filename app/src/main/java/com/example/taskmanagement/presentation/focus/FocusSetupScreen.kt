package com.example.taskmanagement.presentation.focus

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FocusSetupScreen(
    taskTitle: String,
    completedSessions: Int,
    selectedIndex: Int,
    isRunning: Boolean,
    onNavigateBack: () -> Unit,
    onSelectPreset: (Int) -> Unit,
    onStartSession: () -> Unit
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
            HeaderSection(
                completedSessions = completedSessions,
                onNavigateBack = onNavigateBack
            )

            if (taskTitle.isNotBlank()) {
                FocusTaskSection(taskTitle = taskTitle)
            }

            SetupIntroCard()

            PresetSection(
                selectedIndex = selectedIndex,
                isRunning = isRunning,
                onSelectPreset = onSelectPreset
            )

            StartSessionButton(
                onClick = onStartSession
            )
        }
    }
}

@Composable
private fun SetupIntroCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Choose your focus plan",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )

            Text(
                text = "Pick a session type before entering the focus room.",
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun HeaderSection(
    completedSessions: Int,
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Surface1)
                    .border(0.5.dp, BorderSubtle, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Focus Room",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Light,
                    color = TextPrimary,
                    letterSpacing = (-0.5).sp
                )

                Text(
                    text = "set up your session",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Surface2
        ) {
            Column(
                modifier = Modifier
                    .border(0.5.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = completedSessions.toString(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Light,
                    color = GreenBright,
                    lineHeight = 22.sp
                )

                Text(
                    text = "DONE",
                    fontSize = 9.sp,
                    color = TextMuted,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}

@Composable
private fun PresetSection(
    selectedIndex: Int,
    isRunning: Boolean,
    onSelectPreset: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        focusPresets.forEachIndexed { index, preset ->
            PresetRow(
                preset = preset,
                isSelected = selectedIndex == index,
                isEnabled = !isRunning,
                onClick = { onSelectPreset(index) }
            )
        }
    }
}

@Composable
private fun PresetRow(
    preset: FocusPreset,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) GreenMid else BorderSubtle,
        animationSpec = tween(200),
        label = "border"
    )

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Surface2 else Surface1,
        animationSpec = tween(200),
        label = "bg"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 1.2.dp else 0.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(enabled = isEnabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (isSelected) GreenBright else TextDim,
                            CircleShape
                        )
                )

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = preset.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )

                    Text(
                        text = preset.description,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Text(
                text = "${preset.totalMinutes}m",
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
                color = if (isSelected) GreenBright else TextMuted
            )
        }
    }
}

@Composable
private fun FocusTaskSection(
    taskTitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "FOCUSING ON",
                fontSize = 9.sp,
                color = TextMuted,
                letterSpacing = 1.5.sp
            )

            Text(
                text = taskTitle,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = GreenBright
            )
        }
    }
}

@Composable
private fun StartSessionButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GreenBright)
            .border(1.dp, GreenMid, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 17.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Enter Focus Room",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = BgDeep,
            letterSpacing = 0.5.sp
        )
    }
}