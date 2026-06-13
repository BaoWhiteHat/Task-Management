package com.example.taskmanagement.presentation.focus

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskmanagement.R
import kotlin.math.roundToInt

private val HpRed = Color(0xFFE5484D)
private val HpTrack = Color(0xFF2A1418)
private val Pixel = FontFamily.Monospace

private val campTips = listOf(
    "Tip: Higher-priority tasks spawn tougher foes — but reward double.",
    "Tip: Fleeing mid-battle leaves a dead tree in your forest.",
    "Tip: Keep a daily streak to keep your forest alive.",
    "Tip: A 50-minute session grows the tallest ancient tree.",
    "Tip: Defeat the Deadline Demon before it defeats you.",
    "Tip: Every victory plants another tree in your forest.",
    "Tip: Rest well — a full break makes the next fight easier.",
    "Tip: Spend coins from each victory to unlock new sounds."
)

@Composable
fun FocusBattleScreen(
    state: FocusUiState,
    timeText: String,
    encounter: Encounter,
    onBackToSetup: () -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onConfirmReset: () -> Unit,
    onDismissPenalty: () -> Unit,
    onSkip: () -> Unit
) {
    val hasStarted = state.timeLeft < state.selectedPreset.studySeconds || state.isBreak

    Box(modifier = Modifier.fillMaxSize().background(BgDeep)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 100.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().widthIn(max = 460.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BattleTopBar(onBackToSetup = onBackToSetup)
                HeroHud(profile = state.gameProfile)

                if (state.isBreak) {
                    CampPanel(state = state, timeText = timeText)
                } else {
                    EnemyPanel(
                        state = state,
                        timeText = timeText,
                        encounter = encounter,
                        isRunning = state.isRunning,
                        hasStarted = hasStarted
                    )
                }

                state.selectedSoundId?.let { AmbientPill(soundId = it) }

                BattleControls(
                    isRunning = state.isRunning,
                    hasStarted = hasStarted,
                    isBreak = state.isBreak,
                    onStart = onStart,
                    onPause = onPause,
                    onReset = onReset,
                    onSkip = onSkip
                )
            }
        }

        if (state.showPenaltyWarning) {
            RetreatDialog(onConfirm = onConfirmReset, onDismiss = onDismissPenalty)
        }
    }
}

@Composable
private fun BattleTopBar(onBackToSetup: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Surface1)
                .border(0.5.dp, BorderSubtle, CircleShape)
                .clickable(onClick = onBackToSetup),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.KeyboardArrowLeft, "Back", tint = TextPrimary)
        }
        Text(
            text = "FOCUS QUEST",
            fontFamily = Pixel,
            fontSize = 13.sp,
            color = GreenBright,
            letterSpacing = 2.sp
        )
        Spacer(Modifier.size(40.dp))
    }
}

@Composable
private fun HeroHud(profile: com.example.taskmanagement.data.local.models.GameProfile?) {
    val level = profile?.level ?: 1
    val title = profile?.title ?: "Seedling"
    val xpProgress = profile?.xpProgress ?: 0f
    val xpText = if (profile != null) "${profile.xp} / ${profile.xpForNextLevel} xp" else "0 / 100 xp"
    val coins = profile?.coins ?: 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Surface2)
                .border(0.5.dp, GreenDark, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) { Text("\uD83E\uDDD9", fontSize = 18.sp) }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Lv $level · $title", fontFamily = Pixel, fontSize = 11.sp, color = TextPrimary)
                Text(xpText, fontFamily = Pixel, fontSize = 10.sp, color = TextMuted)
            }
            Spacer(Modifier.height(4.dp))
            StatBar(progress = xpProgress, fill = GreenBright, track = Surface2)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("\uD83E\uDE99", fontSize = 13.sp)
            Spacer(Modifier.width(3.dp))
            Text("$coins", fontFamily = Pixel, fontSize = 11.sp, color = AmberAccent)
        }
    }
}

@Composable
private fun EnemyPanel(
    state: FocusUiState,
    timeText: String,
    encounter: Encounter,
    isRunning: Boolean,
    hasStarted: Boolean
) {
    val enemyHp = state.remainingProgress
    val hpPercent = (enemyHp * 100).toInt()

    val rewardXp = (state.selectedPreset.xpReward * encounter.rank.xpMultiplier).toInt()
    val rewardCoin = (state.selectedPreset.coinReward * encounter.rank.coinMultiplier).toInt()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Surface1)
            .border(0.5.dp, encounter.type.panelBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = encounter.title,
                        fontFamily = Pixel,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = encounter.enemyName.lowercase(),
                        fontFamily = Pixel,
                        fontSize = 10.sp,
                        color = encounter.type.accent,
                        letterSpacing = 1.sp
                    )
                }
                if (encounter.isBoss) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x33E5484D))
                            .border(0.5.dp, HpRed, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) { Text("BOSS", fontFamily = Pixel, fontSize = 10.sp, color = HpRed, letterSpacing = 1.sp) }
                }
            }

            // Enemy HP bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ENEMY HP", fontFamily = Pixel, fontSize = 9.sp, color = TextMuted, letterSpacing = 1.5.sp)
                Text("$hpPercent%", fontFamily = Pixel, fontSize = 9.sp, color = HpRed)
            }
            StatBar(progress = enemyHp, fill = HpRed, track = HpTrack, height = 8.dp)

            // Battlefield: hero vs enemy
            val nudge = attackNudge(active = isRunning)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SpriteSlot(sprite = "\uD83E\uDDD9", label = "you", accent = GreenBright, size = 44.dp, dx = nudge)
                Text("\u2694", fontSize = 18.sp, color = AmberAccent)
                SpriteSlot(
                    sprite = encounter.type.sprite,
                    label = encounter.rank.label.lowercase(),
                    accent = encounter.type.accent,
                    size = if (encounter.isBoss) 56.dp else 48.dp,
                    dx = -nudge
                )
            }

            // Timer = battle clock
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = timeText,
                    fontFamily = Pixel,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    letterSpacing = 2.sp
                )
                val status = when {
                    isRunning -> "attacking…"
                    hasStarted -> "paused"
                    else -> "ready to engage"
                }
                Text(
                    text = "\u2014 $status \u2014",
                    fontFamily = Pixel,
                    fontSize = 10.sp,
                    color = if (isRunning) GreenBright else TextMuted,
                    letterSpacing = 2.sp
                )
            }

            StatBar(progress = state.elapsedProgress, fill = GreenBright, track = Surface2, height = 4.dp)

            // Victory reward (scales with priority)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("victory reward:", fontFamily = Pixel, fontSize = 10.sp, color = TextDim)
                Spacer(Modifier.width(8.dp))
                Text("\u26A1 $rewardXp xp", fontFamily = Pixel, fontSize = 10.sp, color = GreenBright)
                Spacer(Modifier.width(10.dp))
                Text("\uD83E\uDE99 $rewardCoin", fontFamily = Pixel, fontSize = 10.sp, color = AmberAccent)
            }
        }
    }
}

@Composable
private fun CampPanel(state: FocusUiState, timeText: String) {
    val progress = state.elapsedProgress
    // Longer study session -> taller tree (25m -> max stage 6, 50m -> stage 8)
    val targetStage = if (state.selectedPreset.studyMinutes >= 50) 8 else 6
    val currentStage = (1 + progress * (targetStage - 1)).roundToInt().coerceIn(1, targetStage)

    val tip = remember { campTips.random() }

    val animHeight by animateFloatAsState(
        targetValue = 46f + progress * (40f + targetStage * 12f),
        animationSpec = tween(500),
        label = "tree-grow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Surface1)
            .border(0.5.dp, AmberAccent, RoundedCornerShape(18.dp))
            .padding(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("RESTING AT CAMP", fontFamily = Pixel, fontSize = 12.sp, color = AmberAccent, letterSpacing = 2.sp)

            Box(
                modifier = Modifier.fillMaxWidth().height(190.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(GreenDark)
                )
                Image(
                    painter = painterResource(treeStageRes(currentStage)),
                    contentDescription = "Growing tree stage $currentStage",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.height(animHeight.dp)
                )
            }

            Text("\u2014 your tree is growing \u2014", fontFamily = Pixel, fontSize = 10.sp, color = TextMuted, letterSpacing = 2.sp)

            Text(
                text = tip,
                fontFamily = Pixel,
                fontSize = 10.sp,
                color = TextMuted,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )

            Text(
                text = timeText,
                fontFamily = Pixel,
                fontSize = 40.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                letterSpacing = 2.sp
            )
            StatBar(progress = progress, fill = AmberAccent, track = Surface2, height = 4.dp)
        }
    }
}

@Composable
private fun SpriteSlot(sprite: String, label: String, accent: Color, size: Dp, dx: Dp) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .offset(x = dx)
                .size(size)
                .clip(RoundedCornerShape(10.dp))
                .background(Surface2)
                .border(0.5.dp, accent, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) { Text(sprite, fontSize = (size.value * 0.55f).sp) }
        Spacer(Modifier.height(5.dp))
        Text(label, fontFamily = Pixel, fontSize = 9.sp, color = accent, letterSpacing = 1.sp)
    }
}

@Composable
private fun attackNudge(active: Boolean): Dp {
    if (!active) return 0.dp
    val t = rememberInfiniteTransition(label = "atk")
    val v by t.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(620), RepeatMode.Reverse),
        label = "nudge"
    )
    return v.dp
}

@Composable
private fun StatBar(progress: Float, fill: Color, track: Color, height: Dp = 5.dp) {
    val anim by animateFloatAsState(progress.coerceIn(0f, 1f), tween(500), label = "bar")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(track)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(anim)
                .clip(RoundedCornerShape(50))
                .background(fill)
        )
    }
}

@Composable
private fun AmbientPill(soundId: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("\uD83C\uDF27", fontSize = 14.sp)
        Text("$soundId · playing", fontFamily = Pixel, fontSize = 11.sp, color = TextMuted)
    }
}

@Composable
private fun BattleControls(
    isRunning: Boolean,
    hasStarted: Boolean,
    isBreak: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onSkip: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val primaryLabel = when {
            isBreak && isRunning -> "Resting…"
            isBreak -> "Rest"
            isRunning -> "Hold the line"
            hasStarted -> "Resume attack"
            else -> "Engage"
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (isRunning) Surface2 else GreenBright)
                .then(if (isRunning) Modifier.border(0.5.dp, AmberAccent, RoundedCornerShape(14.dp)) else Modifier)
                .clickable(onClick = if (isRunning) onPause else onStart)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = primaryLabel,
                fontFamily = Pixel,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isRunning) AmberAccent else BgDeep,
                letterSpacing = 1.sp
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GhostButton("\u21BA Retreat", onReset, Modifier.weight(1f))
            GhostButton("Skip \u25B8", onSkip, Modifier.weight(1f))
        }
    }
}

@Composable
private fun GhostButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center
    ) { Text(text, fontFamily = Pixel, fontSize = 12.sp, color = TextMuted) }
}

@Composable
private fun RetreatDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(28.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Surface1)
                .border(0.5.dp, HpRed, RoundedCornerShape(18.dp))
                .padding(22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Flee the battle?", fontFamily = Pixel, fontSize = 16.sp, color = TextPrimary)
                Text(
                    "Run away will lose process and get penalty",
                    fontFamily = Pixel,
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Surface2)
                            .border(0.5.dp, BorderSubtle, RoundedCornerShape(12.dp))
                            .clickable(onClick = onDismiss)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("Stay", fontFamily = Pixel, fontSize = 12.sp, color = GreenBright) }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(HpRed)
                            .clickable(onClick = onConfirm)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("Flee", fontFamily = Pixel, fontSize = 12.sp, color = Color.White) }
                }
            }
        }
    }
}

private fun treeStageRes(stage: Int): Int = when (stage.coerceIn(1, 8)) {
    1 -> R.drawable.tree_stage_1
    2 -> R.drawable.tree_stage_2
    3 -> R.drawable.tree_stage_3
    4 -> R.drawable.tree_stage_4
    5 -> R.drawable.tree_stage_5
    6 -> R.drawable.tree_stage_6
    7 -> R.drawable.tree_stage_7
    else -> R.drawable.tree_stage_8
}