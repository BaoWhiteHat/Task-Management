package com.example.taskmanagement.presentation.hub

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanagement.R
import com.example.taskmanagement.presentation.achievements.AchievementsViewModel
import com.example.taskmanagement.presentation.focus.ForestViewModel
import com.example.taskmanagement.presentation.focus.TextMuted
import com.example.taskmanagement.presentation.focus.TextPrimary
import com.example.taskmanagement.presentation.loot.LootCollectionDialog
import com.example.taskmanagement.presentation.loot.LootViewModel
import com.example.taskmanagement.presentation.quest.QuestBoardDialog
import com.example.taskmanagement.presentation.quest.QuestViewModel
import com.example.taskmanagement.presentation.ui.theme.AppearanceState
import com.example.taskmanagement.presentation.ui.theme.TaskTheme
import com.example.taskmanagement.presentation.ui.theme.summary

@Composable
fun HubScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onOpenNotebook: () -> Unit = {},
    onOpenAchievements: () -> Unit = {},
    onOpenShop: () -> Unit = {},
    onStartFocus: () -> Unit = {},
    appearanceState: AppearanceState = AppearanceState(),
    onOpenAppearance: () -> Unit = {},
    viewModel: ForestViewModel = viewModel(),
    lootViewModel: LootViewModel = viewModel(),
    questViewModel: QuestViewModel = viewModel(),
    achievementsViewModel: AchievementsViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.load(context)
        lootViewModel.start(context)
        questViewModel.start(context)
        achievementsViewModel.load(context)
    }
    val profile by viewModel.profile.collectAsState()
    val questClaimable by questViewModel.anyClaimable.collectAsState()
    val achState by achievementsViewModel.ui.collectAsState()

    var satchelOpen by remember { mutableStateOf(false) }
    var questOpen by remember { mutableStateOf(false) }

    val level = profile?.level ?: 1
    val coins = profile?.coins ?: 0
    val title = profile?.title ?: "Seedling"
    val xpProgress = profile?.xpProgress ?: 0f
    val xp = profile?.xp ?: 0
    val xpNext = profile?.xpForNextLevel ?: 100
    val achSubtitle = if (achState.totalCount > 0) "${achState.unlockedCount} / ${achState.totalCount} unlocked" else "Badges you've earned"

    val scheme = MaterialTheme.colorScheme
    val taskColors = TaskTheme.colors
    val appFont = TaskTheme.fontFamily

    Box(modifier = modifier.fillMaxSize().background(scheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {

            // ---- Top bar ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(scheme.surface)
                        .border(0.5.dp, scheme.outline, CircleShape)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) { Text("\u2190", fontSize = 17.sp, color = scheme.onSurface) }

                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("CAMP", fontFamily = appFont, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = scheme.onBackground, letterSpacing = 3.sp)
                    Text("your hero & quests", fontFamily = appFont, fontSize = 10.sp, color = taskColors.subText)
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(scheme.surface)
                        .border(0.5.dp, scheme.outline, CircleShape)
                        .clickable { onOpenNotebook() },
                    contentAlignment = Alignment.Center
                ) { Text("\uD83D\uDCD6", fontSize = 16.sp) }

                Spacer(Modifier.width(8.dp))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(taskColors.accentColor.copy(alpha = 0.15f))
                        .border(0.5.dp, taskColors.accentColor.copy(alpha = 0.6f), RoundedCornerShape(50))
                        .clickable { onOpenShop() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("\uD83E\uDE99", fontSize = 12.sp)
                    Spacer(Modifier.width(4.dp))
                    Text("$coins", fontFamily = appFont, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = taskColors.accentColor)
                    Spacer(Modifier.width(5.dp))
                    Text("\u203A", fontFamily = appFont, fontSize = 14.sp, color = taskColors.accentColor.copy(alpha = 0.7f))
                }
            }

            // ---- Scrollable content (top-aligned, no dead space) ----
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 148.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(Modifier.height(2.dp))

                // ===== HERO SCENE BANNER =====
                HeroBanner(
                    level = level,
                    title = title,
                    xpProgress = xpProgress,
                    xp = xp,
                    xpNext = xpNext
                )

                // ADVENTURE
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionLabel("ADVENTURE")
                    Spacer(Modifier.height(4.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HubCard(
                            icon = "\uD83D\uDCDC",
                            title = "Quests",
                            subtitle = "Daily & weekly goals",
                            badge = questClaimable,
                            onClick = { questOpen = true }
                        )
                        HubCard(
                            icon = "\uD83C\uDFC6",
                            title = "Achievements",
                            subtitle = achSubtitle,
                            onClick = onOpenAchievements
                        )
                        HubCard(
                            icon = "\uD83D\uDCD6",
                            title = "Adventurer's Notebook",
                            subtitle = "Learn how quests, battles, rewards, and guardian items work.",
                            onClick = onOpenNotebook
                        )
                    }
                }

                // COLLECTION
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionLabel("COLLECTION")
                    Spacer(Modifier.height(4.dp))
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        HubCard(
                            icon = "\uD83C\uDF92",
                            title = "Satchel",
                            subtitle = "Your loot collection",
                            onClick = { satchelOpen = true }
                        )
                    }
                }

                // APPEARANCE
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionLabel("APPEARANCE")
                    Spacer(Modifier.height(4.dp))
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        AppearanceSettingsRow(
                            subtitle = appearanceState.summary(),
                            onClick = onOpenAppearance
                        )
                    }
                }
            }
        }
    }

    if (satchelOpen) {
        LootCollectionDialog(viewModel = lootViewModel, onClose = { satchelOpen = false })
    }
    if (questOpen) {
        QuestBoardDialog(
            viewModel = questViewModel,
            onClose = { questOpen = false },
            onFocus = { questOpen = false; onStartFocus() }
        )
    }
}

@Composable
private fun AppearanceSettingsRow(
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Appearance",
                fontFamily = TaskTheme.fontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                subtitle,
                fontFamily = TaskTheme.fontFamily,
                fontSize = 10.sp,
                color = TaskTheme.colors.subText
            )
        }
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = TaskTheme.colors.subText
        )
    }
}

@Composable
private fun HeroBanner(
    level: Int,
    title: String,
    xpProgress: Float,
    xp: Int,
    xpNext: Int
) {
    val scheme = MaterialTheme.colorScheme
    val appFont = TaskTheme.fontFamily
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(236.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(0.5.dp, scheme.outline, RoundedCornerShape(20.dp))
    ) {
        Image(
            painter = painterResource(R.drawable.focus_bg_night),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        0.40f to Color(0x33060D04),
                        0.75f to Color(0xAA060D04),
                        1.0f to Color(0xF2060D04)
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    bitmap = ImageBitmap.imageResource(heroTreeRes(level)),
                    contentDescription = null,
                    filterQuality = FilterQuality.None,
                    modifier = Modifier.height(108.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xCC060D04))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(scheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$level", fontFamily = appFont, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = scheme.onPrimary)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontFamily = appFont, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Level $level", fontFamily = appFont, fontSize = 11.sp, color = TextMuted)
                    }
                }

                Spacer(Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0x99162610))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(xpProgress)
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(scheme.primary)
                    )
                }
                Spacer(Modifier.height(5.dp))
                Text("$xp / $xpNext XP", fontFamily = appFont, fontSize = 10.sp, color = TextMuted)
            }
        }
    }
}

private fun heroTreeRes(level: Int): Int = when (level.coerceIn(1, 8)) {
    1 -> R.drawable.tree_stage_1
    2 -> R.drawable.tree_stage_2
    3 -> R.drawable.tree_stage_3
    4 -> R.drawable.tree_stage_4
    5 -> R.drawable.tree_stage_5
    6 -> R.drawable.tree_stage_6
    7 -> R.drawable.tree_stage_7
    else -> R.drawable.tree_stage_8
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        fontFamily = TaskTheme.fontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold,
        color = TaskTheme.colors.subText, letterSpacing = 2.sp,
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)
    )
}

@Composable
private fun HubCard(
    icon: String,
    title: String,
    subtitle: String,
    badge: Boolean = false,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val taskColors = TaskTheme.colors
    val appFont = TaskTheme.fontFamily
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(scheme.surface)
            .border(0.5.dp, scheme.outline, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(scheme.surfaceVariant)
                    .border(0.5.dp, scheme.outline, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Text(icon, fontSize = 20.sp) }
            if (badge) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(11.dp)
                        .clip(CircleShape)
                        .background(scheme.primary)
                        .border(1.5.dp, scheme.surface, CircleShape)
                )
            }
        }

        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontFamily = appFont, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = scheme.onSurface)
            Text(subtitle, fontFamily = appFont, fontSize = 10.sp, color = taskColors.subText)
        }
        Text("\u203A", fontFamily = appFont, fontSize = 20.sp, color = scheme.primary)
    }
}
