package com.example.taskmanagement.presentation.quest

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanagement.presentation.focus.AmberAccent
import com.example.taskmanagement.presentation.focus.BgDeep
import com.example.taskmanagement.presentation.focus.BorderSubtle
import com.example.taskmanagement.presentation.focus.GreenBright
import com.example.taskmanagement.presentation.focus.GreenDark
import com.example.taskmanagement.presentation.focus.Surface1
import com.example.taskmanagement.presentation.focus.Surface2
import com.example.taskmanagement.presentation.focus.TextDim
import com.example.taskmanagement.presentation.focus.TextMuted
import com.example.taskmanagement.presentation.focus.TextPrimary
import com.example.taskmanagement.presentation.loot.LootRarity
import kotlin.math.roundToInt
import androidx.compose.foundation.layout.systemBarsPadding

private val Pixel = FontFamily.Monospace

private fun rarityColor(r: LootRarity): Color = when (r) {
    LootRarity.COMMON -> Color(0xFF9AA4AE)
    LootRarity.UNCOMMON -> Color(0xFF4ADE80)
    LootRarity.RARE -> Color(0xFF60A5FA)
    LootRarity.EPIC -> Color(0xFFC084FC)
}

@Composable
fun QuestBoardButton(
    modifier: Modifier = Modifier,
    onStartFocus: () -> Unit = {},
    viewModel: QuestViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.start(context) }
    val claimable by viewModel.anyClaimable.collectAsState()
    var open by remember { mutableStateOf(false) }

    Box(modifier = modifier.size(40.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Surface1)
                .border(0.5.dp, GreenDark, CircleShape)
                .clickable { open = true },
            contentAlignment = Alignment.Center
        ) { Text("\uD83D\uDCDC", fontSize = 18.sp) }
        if (claimable) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(GreenBright)
                    .border(1.5.dp, BgDeep, CircleShape)
            )
        }
    }

    if (open) {
        QuestBoardDialog(
            viewModel = viewModel,
            onClose = { open = false },
            onFocus = { open = false; onStartFocus() }
        )
    }
}

@Composable
fun QuestBoardDialog(
    viewModel: QuestViewModel,
    onClose: () -> Unit,
    onFocus: () -> Unit
) {
    val context = LocalContext.current
    val board by viewModel.board.collectAsState()
    val coins by viewModel.coins.collectAsState()
    var weekly by remember { mutableStateOf(false) }

    val period = if (weekly) QuestPeriod.WEEKLY else QuestPeriod.DAILY
    val list = if (weekly) board.weekly else board.daily
    val track = if (weekly) board.weeklyTrack else board.dailyTrack
    val resetText = remember(weekly) { resetLabel(period) }

    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDeep)
        ) {
            Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {

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
                            .clickable { onClose() },
                        contentAlignment = Alignment.Center
                    ) { Text("\u2190", fontSize = 17.sp, color = TextPrimary) }

                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("QUESTS", fontFamily = Pixel, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 2.sp)
                        Text("earn rewards by focusing", fontFamily = Pixel, fontSize = 10.sp, color = TextMuted)
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(AmberAccent.copy(alpha = 0.15f))
                            .border(0.5.dp, AmberAccent.copy(alpha = 0.6f), RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("\uD83E\uDE99", fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text("$coins", fontFamily = Pixel, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AmberAccent)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TabButton("DAILY", "${board.dailyTrack?.done ?: 0} / ${board.dailyTrack?.total ?: 0}", !weekly, Modifier.weight(1f)) { weekly = false }
                    TabButton("WEEKLY", "${board.weeklyTrack?.done ?: 0} / ${board.weeklyTrack?.total ?: 0}", weekly, Modifier.weight(1f)) { weekly = true }
                }

                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (track != null) {
                            TrackRow(track = track, resetText = resetText) { viewModel.claimBonus(context, period) }
                        }
                        list.forEach { q ->
                            QuestCard(
                                ui = q,
                                onClaim = { viewModel.claim(context, q.quest.id) },
                                onFocus = onFocus
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabButton(label: String, sub: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) GreenBright else Surface1)
            .border(
                width = if (selected) 0.dp else 0.5.dp,
                color = if (selected) Color.Transparent else GreenDark,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontFamily = Pixel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (selected) BgDeep else TextPrimary, letterSpacing = 1.sp)
        Text(sub, fontFamily = Pixel, fontSize = 10.sp, color = if (selected) BgDeep.copy(alpha = 0.7f) else TextMuted)
    }
}

@Composable
private fun TrackRow(track: TrackState, resetText: String, onClaimBonus: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("REWARD TRACK \u00B7 ${track.done}/${track.total}", fontFamily = Pixel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 1.sp)
            Spacer(Modifier.weight(1f))
            Text("\u23F1 $resetText", fontFamily = Pixel, fontSize = 10.sp, color = AmberAccent)
        }

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            for (i in 0 until track.total) {
                val done = i < track.done
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (done) GreenBright else Surface2)
                        .border(0.5.dp, if (done) GreenBright else BorderSubtle, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (done) "\u2713" else "${i + 1}", fontFamily = Pixel, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (done) BgDeep else TextDim)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(if (done) GreenDark else BorderSubtle)
                )
            }
            val chestBg = when {
                track.bonusClaimed -> GreenBright
                track.bonusClaimable -> AmberAccent
                else -> Surface2
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(chestBg)
                    .border(0.5.dp, if (track.bonusClaimable || track.bonusClaimed) Color.Transparent else BorderSubtle, CircleShape)
                    .then(if (track.bonusClaimable) Modifier.clickable { onClaimBonus() } else Modifier),
                contentAlignment = Alignment.Center
            ) {
                Text(if (track.bonusClaimed) "\u2713" else "\uD83C\uDF81", fontSize = 16.sp)
            }
        }

        if (track.bonusClaimable) {
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AmberAccent)
                    .clickable { onClaimBonus() }
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Claim bonus  +${track.bonus.coins} \uD83E\uDE99 + ${track.bonus.rewardRarity.label}",
                    fontFamily = Pixel, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BgDeep
                )
            }
        }
    }
}

@Composable
private fun QuestCard(ui: QuestUi, onClaim: () -> Unit, onFocus: () -> Unit) {
    val q = ui.quest
    val context = LocalContext.current
    val iconRes = remember(q.iconDrawable) {
        if (q.iconDrawable.isBlank()) 0
        else context.resources.getIdentifier(q.iconDrawable, "drawable", context.packageName)
    }
    val fraction = if (q.target > 0) (ui.progress.toFloat() / q.target).coerceIn(0f, 1f) else 0f
    val segments = q.target.coerceIn(2, 8)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Surface2)
                    .border(0.5.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (iconRes != 0) {
                    Image(
                        bitmap = ImageBitmap.imageResource(iconRes),
                        contentDescription = null,
                        filterQuality = FilterQuality.None,
                        modifier = Modifier.size(30.dp)
                    )
                } else {
                    Text(if (q.metric == QuestMetric.MINUTES) "\u23F1" else "\u25CE", fontSize = 18.sp)
                }
            }

            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(q.title, fontFamily = Pixel, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(if (q.period == QuestPeriod.DAILY) "today" else "this week", fontFamily = Pixel, fontSize = 10.sp, color = TextMuted)
            }
            Text(
                "${ui.progress.coerceAtMost(q.target)} / ${q.target}",
                fontFamily = Pixel, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                color = if (ui.progress >= q.target) GreenBright else TextMuted
            )
        }

        Spacer(Modifier.height(12.dp))
        SegmentedBar(fraction = fraction, segments = segments)
        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            RewardChip("+${q.coins}", "\uD83E\uDE99", AmberAccent)
            q.rewardRarity?.let {
                Spacer(Modifier.width(8.dp))
                RewardChip(it.label, "\uD83C\uDF92", rarityColor(it))
            }
            Spacer(Modifier.weight(1f))
            QuestAction(ui = ui, onClaim = onClaim, onFocus = onFocus)
        }
    }
}

@Composable
private fun SegmentedBar(fraction: Float, segments: Int) {
    val filled = (fraction * segments).roundToInt().coerceIn(0, segments)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        for (i in 0 until segments) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (i < filled) GreenBright else Surface2)
            )
        }
    }
}

@Composable
private fun RewardChip(text: String, leading: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.15f))
            .border(0.5.dp, color.copy(alpha = 0.5f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(leading, fontSize = 11.sp)
        Spacer(Modifier.width(4.dp))
        Text(text, fontFamily = Pixel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun QuestAction(ui: QuestUi, onClaim: () -> Unit, onFocus: () -> Unit) {
    when {
        ui.claimed -> Row(verticalAlignment = Alignment.CenterVertically) {
            Text("\u2713 ", fontFamily = Pixel, color = GreenBright, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("Claimed", fontFamily = Pixel, fontSize = 12.sp, color = TextMuted)
        }
        ui.claimable -> Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(GreenBright)
                .clickable { onClaim() }
                .padding(horizontal = 22.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center
        ) { Text("Claim", fontFamily = Pixel, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BgDeep) }
        else -> Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(0.5.dp, GreenDark, RoundedCornerShape(12.dp))
                .clickable { onFocus() }
                .padding(horizontal = 16.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center
        ) { Text("Focus \u203A", fontFamily = Pixel, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GreenBright) }
    }
}