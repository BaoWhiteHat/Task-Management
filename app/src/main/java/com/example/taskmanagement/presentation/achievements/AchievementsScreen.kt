package com.example.taskmanagement.presentation.achievements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanagement.data.local.models.Achievement
import com.example.taskmanagement.presentation.focus.BgDeep
import com.example.taskmanagement.presentation.focus.BorderSubtle
import com.example.taskmanagement.presentation.focus.GreenBright
import com.example.taskmanagement.presentation.focus.GreenDark
import com.example.taskmanagement.presentation.focus.Surface1
import com.example.taskmanagement.presentation.focus.Surface2
import com.example.taskmanagement.presentation.focus.TextDim
import com.example.taskmanagement.presentation.focus.TextMuted
import com.example.taskmanagement.presentation.focus.TextPrimary

private val Mono = FontFamily.Monospace

private val RarityCommon    = Color(0xFF8FA77B)
private val RarityRare      = Color(0xFF4FA3E0)
private val RarityEpic      = Color(0xFFB061E0)
private val RarityLegendary = Color(0xFFFFB020)

private fun rarityColor(rarity: String): Color = when (rarity) {
    Rarity.LEGENDARY -> RarityLegendary
    Rarity.EPIC      -> RarityEpic
    Rarity.RARE      -> RarityRare
    else             -> RarityCommon
}

@Composable
fun AchievementsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AchievementsViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.load(context) }

    val ui by viewModel.ui.collectAsState()

    var mode by remember { mutableStateOf("grid") }
    var filter by remember { mutableStateOf("All") }
    var selected by remember { mutableStateOf<Achievement?>(null) }

    val categories = listOf("All", Category.FOCUS, Category.TASKS, Category.STREAK, Category.LEVEL)
    val shown = if (filter == "All") ui.items else ui.items.filter { it.category == filter }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
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
                        .clickable(onClick = onNavigateBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, "Back", tint = TextPrimary)
                }
                Text(
                    "ACHIEVEMENTS",
                    fontFamily = Mono,
                    fontSize = 13.sp,
                    color = GreenBright,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.size(40.dp))
            }

            SummaryHeader(ui.unlockedCount, ui.totalCount)

            ModeToggle(mode = mode, onChange = { mode = it })

            if (mode == "grid") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        CategoryChip(
                            label = if (cat == "All") "ALL" else cat.uppercase(),
                            selected = filter == cat,
                            onClick = { filter = cat }
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    shown.chunked(3).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { a ->
                                BadgeCell(
                                    achievement = a,
                                    modifier = Modifier.weight(1f),
                                    onClick = { selected = a }
                                )
                            }
                            repeat(3 - rowItems.size) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            } else {
                JourneyView(
                    items = ui.items,
                    current = ui.currentValues,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    selected?.let { a ->
        DetailDialog(a) { selected = null }
    }

    if (ui.newlyUnlocked.isNotEmpty()) {
        CelebrationDialog(
            items = ui.newlyUnlocked,
            onDismiss = { viewModel.consumeNewlyUnlocked() }
        )
    }
}

@Composable
private fun SummaryHeader(unlocked: Int, total: Int) {
    val fraction = if (total == 0) 0f else unlocked.toFloat() / total
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text("UNLOCKED", fontFamily = Mono, fontSize = 11.sp, color = TextMuted, letterSpacing = 1.sp)
            Text("$unlocked / $total", fontFamily = Mono, fontSize = 16.sp, color = GreenBright)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF17240F))
        ) {
            if (fraction > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Brush.horizontalGradient(listOf(GreenDark, GreenBright)))
                )
            }
        }
    }
}

@Composable
private fun ModeToggle(mode: String, onChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(20.dp))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        ToggleTab("GRID", mode == "grid", Modifier.weight(1f)) { onChange("grid") }
        ToggleTab("JOURNEY", mode == "path", Modifier.weight(1f)) { onChange("path") }
    }
}

@Composable
private fun ToggleTab(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) GreenDark.copy(alpha = 0.4f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontFamily = Mono,
            fontSize = 11.sp,
            color = if (selected) GreenBright else TextMuted,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) GreenDark.copy(alpha = 0.35f) else Surface1)
            .border(
                0.5.dp,
                if (selected) GreenBright else BorderSubtle,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            label,
            fontFamily = Mono,
            fontSize = 11.sp,
            color = if (selected) GreenBright else TextMuted
        )
    }
}

@Composable
private fun BadgeCell(
    achievement: Achievement,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val resId = remember(achievement.iconName) {
        context.resources.getIdentifier(achievement.iconName, "drawable", context.packageName)
    }
    val rc = rarityColor(achievement.rarity)
    val unlocked = achievement.isUnlocked
    val grayscale = remember { ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (unlocked) Surface2 else Surface1)
            .border(
                width = if (unlocked) 1.dp else 0.5.dp,
                color = if (unlocked) rc.copy(alpha = 0.9f) else BorderSubtle,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (resId != 0) {
                Image(
                    painter = painterResource(resId),
                    contentDescription = achievement.title,
                    modifier = Modifier
                        .size(60.dp)
                        .alpha(if (unlocked) 1f else 0.35f),
                    colorFilter = if (unlocked) null else grayscale
                )
            } else {
                FallbackBadge(rc = rc, unlocked = unlocked, letter = achievement.category.take(1), size = 60)
            }
            if (!unlocked) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = TextMuted,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(18.dp)
                )
            }
        }
        Text(
            achievement.title,
            fontFamily = Mono,
            fontSize = 10.sp,
            color = if (unlocked) TextPrimary else TextMuted,
            maxLines = 2,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
        )
        Text(
            achievement.rarity.uppercase(),
            fontFamily = Mono,
            fontSize = 8.sp,
            letterSpacing = 1.sp,
            color = if (unlocked) rc else TextDim
        )
    }
}

@Composable
private fun FallbackBadge(rc: Color, unlocked: Boolean, letter: String, size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(if (unlocked) rc.copy(alpha = 0.22f) else Surface1)
            .border(1.dp, if (unlocked) rc else BorderSubtle, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            letter,
            fontFamily = Mono,
            color = if (unlocked) rc else TextDim,
            fontSize = (size / 3).sp
        )
    }
}

@Composable
private fun BadgeArt(achievement: Achievement, rc: Color, big: Boolean) {
    val context = LocalContext.current
    val resId = remember(achievement.iconName) {
        context.resources.getIdentifier(achievement.iconName, "drawable", context.packageName)
    }
    val size = if (big) 96 else 60
    if (resId != 0) {
        Image(
            painter = painterResource(resId),
            contentDescription = achievement.title,
            modifier = Modifier.size(size.dp)
        )
    } else {
        FallbackBadge(rc = rc, unlocked = true, letter = achievement.category.take(1), size = size)
    }
}

private enum class StepState { DONE, ACTIVE, LOCKED }

private val metricOrder = listOf(
    Metric.SESSIONS, Metric.MINUTES, Metric.TASKS, Metric.STREAK, Metric.LEVEL
)

private fun metricLabel(metric: String): String = when (metric) {
    Metric.SESSIONS -> "Focus Sessions"
    Metric.MINUTES  -> "Focus Time"
    Metric.TASKS    -> "Tasks Completed"
    Metric.STREAK   -> "Daily Streak"
    Metric.LEVEL    -> "Hero Level"
    else            -> metric
}

private fun metricUnit(metric: String): String = when (metric) {
    Metric.MINUTES -> " min"
    Metric.STREAK  -> " days"
    else           -> ""
}

@Composable
private fun JourneyView(items: List<Achievement>, current: Map<String, Int>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        metricOrder.forEach { metric ->
            val chain = items.filter { it.metric == metric }.sortedBy { it.threshold }
            if (chain.isNotEmpty()) {
                JourneyPath(metric = metric, chain = chain, currentValue = current[metric] ?: 0)
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun JourneyPath(metric: String, chain: List<Achievement>, currentValue: Int) {
    val unlockedInChain = chain.count { it.isUnlocked }
    val activeIndex = chain.indexOfFirst { !it.isUnlocked }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                metricLabel(metric).uppercase(),
                fontFamily = Mono,
                fontSize = 12.sp,
                color = GreenBright,
                letterSpacing = 1.sp
            )
            Text("$unlockedInChain / ${chain.size}", fontFamily = Mono, fontSize = 11.sp, color = TextMuted)
        }

        chain.forEachIndexed { i, a ->
            val state = when {
                a.isUnlocked -> StepState.DONE
                i == activeIndex -> StepState.ACTIVE
                else -> StepState.LOCKED
            }
            JourneyStep(
                achievement = a,
                state = state,
                currentValue = currentValue,
                isLastStep = i == chain.size - 1
            )
        }
    }
}

@Composable
private fun JourneyStep(
    achievement: Achievement,
    state: StepState,
    currentValue: Int,
    isLastStep: Boolean
) {
    val rc = rarityColor(achievement.rarity)
    val nodeColor = when (state) {
        StepState.DONE -> rc
        StepState.ACTIVE -> GreenBright
        StepState.LOCKED -> BorderSubtle
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (state == StepState.LOCKED) Surface2 else nodeColor.copy(alpha = 0.22f))
                    .border(1.5.dp, nodeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    StepState.DONE -> Text("✓", fontFamily = Mono, color = nodeColor, fontSize = 16.sp)
                    StepState.ACTIVE -> Text("◆", fontFamily = Mono, color = nodeColor, fontSize = 12.sp)
                    StepState.LOCKED -> Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = TextDim,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            if (!isLastStep) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(38.dp)
                        .background(if (state == StepState.DONE) rc.copy(alpha = 0.5f) else BorderSubtle)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLastStep) 0.dp else 10.dp)
        ) {
            Text(
                achievement.title,
                fontFamily = Mono,
                fontSize = 13.sp,
                color = if (state == StepState.LOCKED) TextDim else TextPrimary
            )
            Text(
                achievement.description,
                fontFamily = Mono,
                fontSize = 10.sp,
                color = TextMuted,
                lineHeight = 13.sp
            )
            when (state) {
                StepState.DONE -> {
                    Spacer(Modifier.height(2.dp))
                    Text("✓ UNLOCKED", fontFamily = Mono, fontSize = 9.sp, color = rc, letterSpacing = 1.sp)
                }
                StepState.ACTIVE -> {
                    val cur = currentValue.coerceAtMost(achievement.threshold)
                    val frac = if (achievement.threshold == 0) 0f
                    else (cur.toFloat() / achievement.threshold).coerceIn(0f, 1f)
                    Spacer(Modifier.height(5.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF17240F))
                    ) {
                        if (frac > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(frac)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Brush.horizontalGradient(listOf(GreenDark, GreenBright)))
                            )
                        }
                    }
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "$cur / ${achievement.threshold}${metricUnit(achievement.metric)}",
                        fontFamily = Mono,
                        fontSize = 10.sp,
                        color = GreenBright
                    )
                }
                StepState.LOCKED -> {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "LOCKED",
                        fontFamily = Mono,
                        fontSize = 9.sp,
                        color = TextDim,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailDialog(a: Achievement, onDismiss: () -> Unit) {
    val rc = rarityColor(a.rarity)
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BgDeep)
                .border(
                    1.dp,
                    rc.copy(alpha = if (a.isUnlocked) 0.9f else 0.4f),
                    RoundedCornerShape(16.dp)
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box {
                BadgeArt(a, rc, big = true)
            }
            Text(
                a.title,
                fontFamily = Mono,
                fontSize = 16.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                "${a.rarity.uppercase()} · ${a.category.uppercase()}",
                fontFamily = Mono,
                fontSize = 10.sp,
                color = rc,
                letterSpacing = 1.sp
            )
            Text(
                a.description,
                fontFamily = Mono,
                fontSize = 12.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
            Text(
                if (a.isUnlocked && a.unlockedAt > 0L) "Unlocked ${formatDate(a.unlockedAt)}" else "Locked",
                fontFamily = Mono,
                fontSize = 10.sp,
                color = TextDim
            )
        }
    }
}

@Composable
internal fun CelebrationDialog(items: List<Achievement>, onDismiss: () -> Unit) {
    var index by remember { mutableStateOf(0) }
    val safeIndex = index.coerceIn(0, items.size - 1)
    val a = items[safeIndex]
    val rc = rarityColor(a.rarity)
    val isLast = safeIndex >= items.size - 1

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BgDeep)
                .border(1.5.dp, rc, RoundedCornerShape(16.dp))
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "ACHIEVEMENT UNLOCKED",
                fontFamily = Mono,
                fontSize = 12.sp,
                color = rc,
                letterSpacing = 2.sp
            )
            BadgeArt(a, rc, big = true)
            Text(
                a.title,
                fontFamily = Mono,
                fontSize = 18.sp,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                a.rarity.uppercase(),
                fontFamily = Mono,
                fontSize = 10.sp,
                color = rc,
                letterSpacing = 1.sp
            )
            Text(
                a.description,
                fontFamily = Mono,
                fontSize = 12.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(rc.copy(alpha = 0.2f))
                    .border(1.dp, rc, RoundedCornerShape(10.dp))
                    .clickable { if (isLast) onDismiss() else index = safeIndex + 1 }
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text(
                    if (isLast) "AWESOME" else "NEXT  (${safeIndex + 1}/${items.size})",
                    fontFamily = Mono,
                    fontSize = 12.sp,
                    color = TextPrimary
                )
            }
        }
    }
}

private fun formatDate(millis: Long): String {
    val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.ENGLISH)
    return sdf.format(java.util.Date(millis))
}