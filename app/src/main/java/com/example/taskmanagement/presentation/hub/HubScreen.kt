package com.example.taskmanagement.presentation.hub

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanagement.presentation.achievements.AchievementsViewModel
import com.example.taskmanagement.presentation.focus.AmberAccent
import com.example.taskmanagement.presentation.focus.BgDeep
import com.example.taskmanagement.presentation.focus.BorderSubtle
import com.example.taskmanagement.presentation.focus.ForestViewModel
import com.example.taskmanagement.presentation.focus.GreenBright
import com.example.taskmanagement.presentation.focus.GreenDark
import com.example.taskmanagement.presentation.focus.Surface1
import com.example.taskmanagement.presentation.focus.Surface2
import com.example.taskmanagement.presentation.focus.TextDim
import com.example.taskmanagement.presentation.focus.TextMuted
import com.example.taskmanagement.presentation.focus.TextPrimary
import com.example.taskmanagement.presentation.loot.LootCollectionDialog
import com.example.taskmanagement.presentation.loot.LootViewModel
import com.example.taskmanagement.presentation.quest.QuestBoardDialog
import com.example.taskmanagement.presentation.quest.QuestViewModel

private val Pixel = FontFamily.Monospace

@Composable
fun HubScreen(
    onNavigateBack: () -> Unit = {},
    onOpenStory: () -> Unit = {},
    onOpenAchievements: () -> Unit = {},
    onOpenShop: () -> Unit = {},
    onStartFocus: () -> Unit = {},
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

    Box(modifier = Modifier.fillMaxSize().background(BgDeep)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {

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
                        .background(Surface1)
                        .border(0.5.dp, GreenDark, CircleShape)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) { Text("\u2190", fontSize = 17.sp, color = TextPrimary) }

                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("CAMP", fontFamily = Pixel, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 3.sp)
                    Text("your hero & quests", fontFamily = Pixel, fontSize = 10.sp, color = TextMuted)
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Surface1)
                        .border(0.5.dp, GreenDark, CircleShape)
                        .clickable { onOpenStory() },
                    contentAlignment = Alignment.Center
                ) { Text("\uD83D\uDCD6", fontSize = 16.sp) }

                Spacer(Modifier.width(8.dp))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(AmberAccent.copy(alpha = 0.15f))
                        .border(0.5.dp, AmberAccent.copy(alpha = 0.6f), RoundedCornerShape(50))
                        .clickable { onOpenShop() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("\uD83E\uDE99", fontSize = 12.sp)
                    Spacer(Modifier.width(4.dp))
                    Text("$coins", fontFamily = Pixel, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AmberAccent)
                    Spacer(Modifier.width(5.dp))
                    Text("\u203A", fontFamily = Pixel, fontSize = 14.sp, color = AmberAccent.copy(alpha = 0.7f))
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
            ) {
                // Hero card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Surface1)
                        .border(0.5.dp, BorderSubtle, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(GreenBright),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$level", fontFamily = Pixel, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BgDeep)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, fontFamily = Pixel, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Level $level", fontFamily = Pixel, fontSize = 11.sp, color = TextMuted)
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Surface2)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(xpProgress)
                                .height(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(GreenBright)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("$xp / $xpNext XP", fontFamily = Pixel, fontSize = 10.sp, color = TextDim)
                }

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
                    }
                }

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
private fun SectionLabel(text: String) {
    Text(
        text,
        fontFamily = Pixel, fontSize = 11.sp, fontWeight = FontWeight.Bold,
        color = TextDim, letterSpacing = 2.sp,
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface2)
                    .border(0.5.dp, BorderSubtle, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Text(icon, fontSize = 20.sp) }
            if (badge) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(11.dp)
                        .clip(CircleShape)
                        .background(GreenBright)
                        .border(1.5.dp, Surface1, CircleShape)
                )
            }
        }

        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontFamily = Pixel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(subtitle, fontFamily = Pixel, fontSize = 10.sp, color = TextMuted)
        }
        Text("\u203A", fontFamily = Pixel, fontSize = 20.sp, color = GreenDark)
    }
}