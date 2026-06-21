package com.example.taskmanagement.presentation.focus

import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanagement.R
import com.example.taskmanagement.presentation.achievements.AchievementsViewModel
import com.example.taskmanagement.presentation.achievements.CelebrationDialog

private val ForestMono = FontFamily.Monospace
private val Fire = Color(0xFFFF8A3D)

@Composable
fun ForestScreen(
    onNavigateBack: () -> Unit = {},
    onOpenAchievements: () -> Unit = {},
    onOpenStory: () -> Unit = {},
    onOpenShop: () -> Unit = {},
    viewModel: ForestViewModel = viewModel(),
    achievementsViewModel: AchievementsViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.load(context) }
    LaunchedEffect(Unit) { achievementsViewModel.load(context) }

    val sessions by viewModel.sessions.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val achState by achievementsViewModel.ui.collectAsState()

    val streak = profile?.streakDays ?: 0
    val bestStreak = profile?.bestStreak ?: 0
    val sessionCount = sessions.size
    val totalMinutes = sessions.sumOf { it.studyMinutes }
    val coins = profile?.coins ?: 0

    val weekCutoff = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
    val weekSessions = sessions.filter { it.completedAtMillis >= weekCutoff }
    val weekMinutes = weekSessions.sumOf { it.studyMinutes }

    Box(modifier = Modifier.fillMaxSize().background(BgDeep)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top bar
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
                ) { Icon(Icons.Default.KeyboardArrowLeft, "Back", tint = TextPrimary) }

                Text("MY PROGRESS", fontFamily = ForestMono, fontSize = 13.sp, color = GreenBright, letterSpacing = 2.sp)
                Spacer(Modifier.size(40.dp))
            }

            // Ring + world tree (decorative)
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(210.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = 10.dp.toPx()
                        val arcTopLeft = Offset(stroke / 2, stroke / 2)
                        val arcSize = Size(size.width - stroke, size.height - stroke)

                        // subtle track
                        drawArc(
                            color = Color(0xFF17240F),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = arcTopLeft,
                            size = arcSize,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
                        )
                        // smooth, calm ring (start = end color -> no visible seam)
                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color(0xFF2E8B6E),
                                    Color(0xFF4FB87E),
                                    Color(0xFF7FD89A),
                                    Color(0xFF4FB87E),
                                    Color(0xFF2E8B6E)
                                )
                            ),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = arcTopLeft,
                            size = arcSize,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.tree_stage_8),
                        contentDescription = "Your world tree",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .height(148.dp)
                    )
                }
            }

            // Primary stat cards
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                StatCell("\uD83D\uDD25", "$streak", "day streak", Fire, Modifier.weight(1f))
                StatCell("\uD83C\uDF31", "$sessionCount", "sessions done", GreenBright, Modifier.weight(1f))
                StatCell("\u23F3", fmtTime(totalMinutes), "focused", AmberAccent, Modifier.weight(1f))
            }

            // Supporting rows
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface1)
                    .border(0.5.dp, BorderSubtle, RoundedCornerShape(12.dp))
            ) {
                InfoRow("Best streak", "$bestStreak days", divider = true)
                InfoRow("This week", "${weekSessions.size} sessions · ${fmtTime(weekMinutes)}", divider = true)
                InfoRow("Coins earned", "\uD83E\uDE99 $coins", divider = false)
            }

            // Achievements entry -> opens AchievementsScreen
            AchievementsCard(
                unlocked = achState.unlockedCount,
                total = achState.totalCount,
                onClick = onOpenAchievements
            )

            // The Legend entry -> opens StoryScreen
            StoryCard(onClick = onOpenStory)

            // Shop entry -> opens ShopScreen
            ShopCard(onClick = onOpenShop)
        }
    }

    // Just-unlocked achievements -> show the celebration popup right here
    if (achState.newlyUnlocked.isNotEmpty()) {
        CelebrationDialog(
            items = achState.newlyUnlocked,
            onDismiss = { achievementsViewModel.consumeNewlyUnlocked() }
        )
    }
}

@Composable
private fun StatCell(emoji: String, value: String, label: String, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(10.dp))
            .padding(vertical = 11.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 18.sp)
        Spacer(Modifier.height(3.dp))
        Text(value, fontFamily = ForestMono, fontSize = 16.sp, color = TextPrimary)
        Text(label, fontFamily = ForestMono, fontSize = 9.sp, color = TextMuted)
    }
}

@Composable
private fun InfoRow(label: String, value: String, divider: Boolean) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontFamily = ForestMono, fontSize = 11.sp, color = TextMuted)
            Text(value, fontFamily = ForestMono, fontSize = 11.sp, color = TextPrimary)
        }
        if (divider) {
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderSubtle))
        }
    }
}

@Composable
private fun AchievementsCard(unlocked: Int, total: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("\uD83C\uDFC6", fontSize = 18.sp)
            Spacer(Modifier.size(10.dp))
            Column {
                Text("Achievements", fontFamily = ForestMono, fontSize = 13.sp, color = TextPrimary)
                Text(
                    if (total > 0) "$unlocked / $total unlocked" else "View your badges",
                    fontFamily = ForestMono,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
        Text("\u203A", fontFamily = ForestMono, fontSize = 20.sp, color = AmberAccent)
    }
}

@Composable
private fun StoryCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("\uD83D\uDCD6", fontSize = 18.sp)
            Spacer(Modifier.size(10.dp))
            Column {
                Text("The Legend", fontFamily = ForestMono, fontSize = 13.sp, color = TextPrimary)
                Text(
                    "Read the story",
                    fontFamily = ForestMono,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
        Text("\u203A", fontFamily = ForestMono, fontSize = 20.sp, color = AmberAccent)
    }
}

@Composable
private fun ShopCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("\uD83D\uDED2", fontSize = 18.sp)
            Spacer(Modifier.size(10.dp))
            Column {
                Text("Shop", fontFamily = ForestMono, fontSize = 13.sp, color = TextPrimary)
                Text(
                    "Spend coins on backgrounds & sounds",
                    fontFamily = ForestMono,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
        Text("\u203A", fontFamily = ForestMono, fontSize = 20.sp, color = AmberAccent)
    }
}

private fun fmtTime(min: Int): String =
    if (min >= 60) "${min / 60}h ${min % 60}m" else "${min}m"