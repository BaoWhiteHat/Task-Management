package com.example.taskmanagement.presentation.focus

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskmanagement.presentation.ui.theme.TaskTheme

@Composable
fun FocusSetupScreen(
    taskTitle: String,
    state: FocusUiState,
    onNavigateBack: () -> Unit,
    onSelectPreset: (Int) -> Unit,
    onSelectSound: (String) -> Unit,
    onOpenForest: () -> Unit = {},
    onStartSession: () -> Unit
) {
    val profile = state.gameProfile
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        HeaderSection(
            coins = profile?.coins ?: 0,
            onNavigateBack = onNavigateBack
        )
        ForestButton(onClick = onOpenForest)
        if (profile != null) {
            XpLevelBar(profile = profile)
        }
        if (taskTitle.isNotBlank()) {
            FocusTaskCard(taskTitle = taskTitle)
        }
        Text(
            text = "Choose your focus plan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Earn XP & coins for every completed session.",
            style = MaterialTheme.typography.bodySmall,
            color = TaskTheme.colors.subText
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            focusPresets.forEachIndexed { index, preset ->
                PresetCard(
                    preset = preset,
                    isSelected = state.selectedPresetIndex == index,
                    isEnabled = !state.isRunning,
                    onClick = { onSelectPreset(index) }
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ambient Sound",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ambientSounds.forEach { sound ->
                val isUnlocked = profile?.hasSound(sound.id) ?: (sound.price == 0)
                val isSelected = state.selectedSoundId == sound.id
                SoundItem(
                    sound = sound,
                    isUnlocked = isUnlocked,
                    isSelected = isSelected,
                    onClick = { if (isUnlocked) onSelectSound(sound.id) }
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = onStartSession)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enter Focus Room",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "${state.selectedPreset.totalMinutes}m",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .7f)
                )
            }
        }
    }
}

@Composable
private fun ForestButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("\uD83C\uDF32", fontSize = 18.sp)
        Column(Modifier.weight(1f)) {
            Text(
                text = "My Forest",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "See every tree you've grown",
                style = MaterialTheme.typography.bodySmall,
                color = TaskTheme.colors.subText
            )
        }
        Text("\u203A", fontSize = 20.sp, color = TaskTheme.colors.subText)
    }
}

@Composable
private fun HeaderSection(
    coins: Int,
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Focus Room",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "set up your session",
                style = MaterialTheme.typography.bodySmall,
                color = TaskTheme.colors.subText
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(TaskTheme.colors.priorityMediumBg)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "\uD83E\uDE99", fontSize = 16.sp)
                Text(
                    text = coins.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TaskTheme.colors.priorityMedium
                )
            }
        }
    }
}

@Composable
private fun XpLevelBar(
    profile: com.example.taskmanagement.data.local.models.GameProfile
) {
    val animatedProgress by animateFloatAsState(
        targetValue = profile.xpProgress,
        animationSpec = tween(600),
        label = "xp"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lv.${profile.level}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = profile.title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = "${profile.xp} / ${profile.xpForNextLevel} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = TaskTheme.colors.subText
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = .2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
            Text(
                text = "${profile.xpForNextLevel - profile.xp} XP to Lv.${profile.level + 1} ${
                    when {
                        profile.level + 1 >= 20 -> "Ancient Tree"
                        profile.level + 1 >= 15 -> "Mighty Oak"
                        profile.level + 1 >= 10 -> "Tree"
                        profile.level + 1 >= 6 -> "Sapling"
                        profile.level + 1 >= 3 -> "Sprout"
                        else -> "Seedling"
                    }
                }",
                style = MaterialTheme.typography.labelSmall,
                color = TaskTheme.colors.subText
            )
        }
    }
}

@Composable
private fun FocusTaskCard(taskTitle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "FOCUSING ON",
                style = MaterialTheme.typography.labelSmall,
                color = TaskTheme.colors.subText,
                letterSpacing = 1.sp
            )
            Text(
                text = taskTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun PresetCard(
    preset: FocusPreset,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline.copy(alpha = .3f),
        animationSpec = tween(200),
        label = "border"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(200),
        label = "bg"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(enabled = isEnabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
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
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline.copy(alpha = .4f),
                            CircleShape
                        )
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = preset.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = preset.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TaskTheme.colors.subText
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${preset.totalMinutes}m",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else TaskTheme.colors.subText
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "+ ${preset.xpReward} XP",
                        style = MaterialTheme.typography.labelSmall,
                        color = TaskTheme.colors.success
                    )
                    Text(
                        text = "+ ${preset.coinReward}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TaskTheme.colors.priorityMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun SoundItem(
    sound: AmbientSound,
    isUnlocked: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(enabled = isUnlocked, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .then(
                    if (isSelected) Modifier.border(
                        2.dp, MaterialTheme.colorScheme.primary, CircleShape
                    ) else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isUnlocked) {
                Text(
                    text = when (sound.id) {
                        "rain" -> "\uD83C\uDF27"
                        "forest" -> "\uD83C\uDF32"
                        "fireplace" -> "\uD83D\uDD25"
                        "ocean" -> "\uD83C\uDF0A"
                        else -> "\uD83C\uDFB5"
                    },
                    fontSize = 22.sp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = TaskTheme.colors.subText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = sound.name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
        Text(
            text = when {
                isSelected -> "Playing"
                isUnlocked -> "Owned"
                else -> "Locked"
            },
            style = MaterialTheme.typography.labelSmall,
            color = if (isUnlocked) TaskTheme.colors.success
            else TaskTheme.colors.subText,
            fontSize = 10.sp
        )
    }
}