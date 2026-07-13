package com.example.taskmanagement.presentation.rewards

import com.example.taskmanagement.presentation.ui.theme.TaskTheme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
import com.example.taskmanagement.presentation.loot.lootItemById
import com.example.taskmanagement.presentation.shop.shopBackgrounds
import com.example.taskmanagement.presentation.shop.shopTomes
import androidx.compose.foundation.shape.CircleShape


@Composable
fun DailyLoginHost(viewModel: DailyLoginViewModel = viewModel()) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.checkLogin(context) }
    val state by viewModel.state.collectAsState()
    if (state.visible) {
        DailyLoginDialog(
            state = state,
            onClaim = { viewModel.claim(context) },
            onClose = { viewModel.dismiss() }
        )
    }
}

@Composable
fun DailyRewardsButton(
    modifier: Modifier = Modifier,
    viewModel: DailyLoginViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.checkLogin(context) }
    val state by viewModel.state.collectAsState()
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
        ) {
            Text("\uD83C\uDF81", fontSize = 18.sp)
        }
        if (!state.claimedToday) {
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

    if (open || state.visible) {
        DailyLoginDialog(
            state = state,
            onClaim = { viewModel.claim(context) },
            onClose = { open = false; viewModel.dismiss() }
        )
    }
}

@Composable
fun DailyLoginDialog(
    state: DailyLoginState,
    onClaim: () -> Unit,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(BgDeep)
                .border(0.5.dp, GreenDark, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "DAILY LOGIN",
                    fontFamily = TaskTheme.fontFamily,
                    fontSize = 15.sp,
                    color = GreenBright,
                    letterSpacing = 3.sp
                )
                Text(
                    text = "Day ${state.cycleDay} of 7  \u00B7  \uD83D\uDD25 ${state.streak}-day streak",
                    fontFamily = TaskTheme.fontFamily,
                    fontSize = 10.sp,
                    color = TextMuted
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (d in 1..3) RewardCell(day = d, state = state, modifier = Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (d in 4..6) RewardCell(day = d, state = state, modifier = Modifier.weight(1f))
                }
                Day7Banner(state = state)

                val claimable = !state.claimedToday
                if (claimable) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(GreenBright)
                            .clickable(onClick = onClaim)
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Claim Day ${state.cycleDay}",
                            fontFamily = TaskTheme.fontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BgDeep,
                            letterSpacing = 1.sp
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GreenDark.copy(alpha = 0.18f))
                            .border(0.5.dp, GreenDark, RoundedCornerShape(12.dp))
                            .clickable(onClick = onClose)
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("\u2713", fontFamily = TaskTheme.fontFamily, fontSize = 13.sp, color = GreenBright)
                        Text(
                            text = "Reward claimed \u00B7 back tomorrow",
                            fontFamily = TaskTheme.fontFamily,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardCell(day: Int, state: DailyLoginState, modifier: Modifier = Modifier) {
    val reward = loginRewards.firstOrNull { it.day == day } ?: return
    val isToday = day == state.cycleDay && !state.claimedToday
    val isClaimed = day < state.cycleDay || (day == state.cycleDay && state.claimedToday)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isToday -> GreenDark.copy(alpha = 0.35f)
                    isClaimed -> Surface2
                    else -> Surface1
                }
            )
            .border(
                width = if (isToday) 1.5.dp else 0.5.dp,
                color = if (isToday) GreenBright else BorderSubtle,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            "DAY $day",
            fontFamily = TaskTheme.fontFamily,
            fontSize = 8.sp,
            color = if (isToday) GreenBright else TextDim,
            letterSpacing = 1.sp
        )
        RewardIcon(reward = reward, size = 30.dp)
        Text(
            text = if (isClaimed) "\u2713" else rewardShort(reward),
            fontFamily = TaskTheme.fontFamily,
            fontSize = 9.sp,
            color = if (isClaimed) GreenBright else TextMuted,
            maxLines = 1
        )
    }
}

@Composable
private fun Day7Banner(state: DailyLoginState) {
    val reward = loginRewards.firstOrNull { it.day == 7 } ?: return
    val isToday = state.cycleDay == 7 && !state.claimedToday
    val isClaimed = state.cycleDay == 7 && state.claimedToday
    val tome = reward.tomeId?.let { id -> shopTomes.firstOrNull { it.id == id } }
    val loot = reward.lootId?.let { id -> lootItemById(id) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isToday) AmberAccent.copy(alpha = 0.18f) else Surface1)
            .border(
                width = if (isToday) 1.5.dp else 0.5.dp,
                color = AmberAccent,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RewardIcon(reward = reward, size = 36.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "DAY 7 \u00B7 GRAND REWARD",
                fontFamily = TaskTheme.fontFamily,
                fontSize = 9.sp,
                color = AmberAccent,
                letterSpacing = 1.5.sp
            )
            val bgName = reward.backgroundId?.let { id ->
                shopBackgrounds.firstOrNull { it.id == id }?.name
            }
            Text(
                text = buildString {
                    if (reward.coins > 0) append("+${reward.coins} coins")
                    if (bgName != null) {
                        if (isNotEmpty()) append("  +  ")
                        append(bgName)
                    } else if (reward.backgroundId != null) {
                        if (isNotEmpty()) append("  +  ")
                        append("new arena")
                    }
                    if (tome != null) {
                        if (isNotEmpty()) append("  +  ")
                        append(tome.name)
                    }
                    if (loot != null) {
                        if (isNotEmpty()) append("  +  ")
                        append(loot.name)
                    }
                },
                fontFamily = TaskTheme.fontFamily,
                fontSize = 11.sp,
                color = TextPrimary,
                maxLines = 1
            )
        }
        if (isClaimed) Text("\u2713", fontFamily = TaskTheme.fontFamily, fontSize = 16.sp, color = GreenBright)
    }
}

@Composable
private fun RewardIcon(reward: LoginReward, size: androidx.compose.ui.unit.Dp) {
    val context = LocalContext.current
    val tome = reward.tomeId?.let { id -> shopTomes.firstOrNull { it.id == id } }
    val loot = reward.lootId?.let { id -> lootItemById(id) }
    // Background -> arena art; else tome art; else loot art.
    val artName = reward.backgroundId ?: tome?.drawableName ?: loot?.drawableName
    val resId = remember(artName) {
        if (artName.isNullOrBlank()) 0
        else context.resources.getIdentifier(artName, "drawable", context.packageName)
    }
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(7.dp))
            .background(Surface2),
        contentAlignment = Alignment.Center
    ) {
        when {
            resId != 0 -> Image(
                painter = painterResource(resId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            reward.backgroundId != null -> Text("\uD83C\uDFDE", fontSize = (size.value * 0.5f).sp)
            tome != null -> Text(tome.emoji, fontSize = (size.value * 0.5f).sp)
            loot != null -> Text("\uD83D\uDC8E", fontSize = (size.value * 0.5f).sp)
            else -> Text("\uD83E\uDE99", fontSize = (size.value * 0.5f).sp)
        }
    }
}

private fun rewardShort(reward: LoginReward): String {
    return when {
        reward.backgroundId != null -> "Arena"
        reward.tomeId != null && reward.coins > 0 -> "+${reward.coins} +tome"
        reward.tomeId != null -> "Tome"
        reward.lootId != null -> lootItemById(reward.lootId)?.rarity?.label ?: "Loot"
        else -> "+${reward.coins}"
    }
}