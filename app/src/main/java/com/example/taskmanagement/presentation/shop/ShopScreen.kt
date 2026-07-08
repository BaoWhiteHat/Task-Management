package com.example.taskmanagement.presentation.shop

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanagement.presentation.focus.AmbientSound
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
import com.example.taskmanagement.presentation.focus.ambientSounds

private val Mono = FontFamily.Monospace
private val PriceRed = Color(0xFFE5705A)

@Composable
fun ShopScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ShopViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.load(context) }

    val profile by viewModel.profile.collectAsState()
    val tomeCounts by viewModel.tomeCounts.collectAsState()
    val unlockedSoundIds by viewModel.unlockedSoundIds.collectAsState()
    val unlockedBackgroundIds by viewModel.unlockedBackgroundIds.collectAsState()
    val guardianItemCounts by viewModel.guardianItemCounts.collectAsState()
    val coins = profile?.coins ?: 0
    val selectedBg = profile?.selectedBackgroundId
        ?.takeIf { it in unlockedBackgroundIds }
        .orEmpty()

    var tab by remember { mutableStateOf("bg") }

    Box(modifier = Modifier.fillMaxSize().background(BgDeep)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top bar with coin balance
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

                Text("SHOP", fontFamily = Mono, fontSize = 13.sp, color = GreenBright, letterSpacing = 2.sp)

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Surface1)
                        .border(0.5.dp, BorderSubtle, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("\uD83E\uDE99", fontSize = 13.sp)
                    Spacer(Modifier.width(4.dp))
                    Text("$coins", fontFamily = Mono, fontSize = 12.sp, color = AmberAccent)
                }
            }

            // Tab toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Surface1)
                    .border(0.5.dp, BorderSubtle, RoundedCornerShape(20.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                ShopTab("SCENES", tab == "bg", Modifier.weight(1f)) { tab = "bg" }
                ShopTab("SOUNDS", tab == "sound", Modifier.weight(1f)) { tab = "sound" }
                ShopTab("TOMES", tab == "tome", Modifier.weight(1f)) { tab = "tome" }
                ShopTab("ITEMS", tab == "items", Modifier.weight(1f)) { tab = "items" }
            }

            if (tab == "bg") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DefaultBgRow(
                        selected = selectedBg.isBlank(),
                        onClick = { viewModel.equipBackground(context, "") }
                    )

                    shopBackgrounds.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { bg ->
                                val owned = bg.id in unlockedBackgroundIds
                                BackgroundCard(
                                    bg = bg,
                                    owned = owned,
                                    selected = selectedBg == bg.id,
                                    canAfford = coins >= bg.price,
                                    lockedByLevel = !owned && (profile?.level ?: 1) < bg.requiredLevel,
                                    modifier = Modifier.weight(1f),
                                    onBuy = { viewModel.buyBackground(context, bg) },
                                    onEquip = { viewModel.equipBackground(context, bg.id) }
                                )
                            }
                            repeat(2 - rowItems.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            } else if (tab == "sound") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ambientSounds.forEach { sound ->
                        SoundRow(
                            sound = sound,
                            owned = sound.id in unlockedSoundIds,
                            canAfford = coins >= sound.price,
                            onBuy = { viewModel.buySound(context, sound) }
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }
            } else if (tab == "tome") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Consumable — used once before a battle. The buff applies when you win.",
                        fontFamily = Mono,
                        fontSize = 10.sp,
                        color = TextDim
                    )
                    shopTomes.groupBy { it.category }.forEach { (category, tomes) ->
                        TomeSectionHeader(category)
                        tomes.forEach { tome ->
                            TomeRow(
                                tome = tome,
                                owned = tomeCounts[tome.id] ?: 0,
                                canAfford = coins >= tome.price,
                                lockedByLevel = (profile?.level ?: 1) < tome.requiredLevel,
                                onBuy = { viewModel.buyTome(context, tome) }
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Guardian Items protect your progress and strengthen future victories.",
                        fontFamily = Mono,
                        fontSize = 10.sp,
                        color = TextDim
                    )
                    guardianItems.forEach { item ->
                        GuardianItemRow(
                            item = item,
                            owned = guardianItemCounts[item.id] ?: 0,
                            canAfford = coins >= item.price,
                            magnetActive = (guardianItemCounts[GuardianItemIds.LOOT_MAGNET_ACTIVE]
                                ?: 0) > 0,
                            onBuy = { viewModel.buyGuardianItem(context, item) },
                            onActivate = { viewModel.activateLootMagnet(context) }
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ShopTab(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
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
private fun DefaultBgRow(selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface1)
            .border(
                if (selected) 1.5.dp else 0.5.dp,
                if (selected) GreenBright else BorderSubtle,
                RoundedCornerShape(12.dp)
            )
            .clickable(enabled = !selected, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("\uD83C\uDF04", fontSize = 18.sp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Default", fontFamily = Mono, fontSize = 13.sp, color = TextPrimary)
                Text("Day / night sky", fontFamily = Mono, fontSize = 10.sp, color = TextMuted)
            }
        }
        if (selected) {
            StatePill("ACTIVE", GreenBright)
        } else {
            StatePill("USE", AmberAccent)
        }
    }
}

@Composable
private fun BackgroundCard(
    bg: ShopBackground,
    owned: Boolean,
    selected: Boolean,
    canAfford: Boolean,
    lockedByLevel: Boolean,
    modifier: Modifier = Modifier,
    onBuy: () -> Unit,
    onEquip: () -> Unit
) {
    val context = LocalContext.current
    val resId = remember(bg.id) {
        context.resources.getIdentifier(bg.id, "drawable", context.packageName)
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Surface1)
            .border(
                width = if (selected) 1.5.dp else 0.5.dp,
                color = if (selected) GreenBright else BorderSubtle,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                when {
                    selected -> {}
                    lockedByLevel -> {}
                    owned -> onEquip()
                    canAfford -> onBuy()
                    else -> {}
                }
            }
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Surface2),
            contentAlignment = Alignment.Center
        ) {
            if (resId != 0) {
                Image(
                    painter = painterResource(resId),
                    contentDescription = bg.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (lockedByLevel) 0.3f else 1f)
                )
            } else {
                Text("?", fontFamily = Mono, color = TextDim, fontSize = 26.sp)
            }
            if (lockedByLevel) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = TextPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Text(
            bg.name,
            fontFamily = Mono,
            fontSize = 11.sp,
            color = TextPrimary,
            maxLines = 1
        )

        when {
            selected -> StatePill("ACTIVE", GreenBright)
            owned -> StatePill("USE", AmberAccent)
            lockedByLevel -> Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, "Locked", tint = TextDim, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    "Reach Lv ${bg.requiredLevel}",
                    fontFamily = Mono,
                    fontSize = 10.sp,
                    color = TextDim
                )
            }
            else -> Row(verticalAlignment = Alignment.CenterVertically) {
                Text("\uD83E\uDE99", fontSize = 12.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    "${bg.price}",
                    fontFamily = Mono,
                    fontSize = 12.sp,
                    color = if (canAfford) AmberAccent else PriceRed
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    if (canAfford) "· buy" else "· need more",
                    fontFamily = Mono,
                    fontSize = 9.sp,
                    color = TextDim
                )
            }
        }
    }
}

@Composable
private fun SoundRow(
    sound: AmbientSound,
    owned: Boolean,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .clickable(enabled = !owned && canAfford, onClick = onBuy)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Surface2),
                contentAlignment = Alignment.Center
            ) {
                if (owned) {
                    Text(
                        when (sound.id) {
                            "rain" -> "\uD83C\uDF27"
                            "forest" -> "\uD83C\uDF32"
                            "ocean" -> "\uD83C\uDF0A"
                            else -> "\uD83C\uDFB5"
                        },
                        fontSize = 18.sp
                    )
                } else {
                    Icon(Icons.Default.Lock, "Locked", tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(sound.name, fontFamily = Mono, fontSize = 13.sp, color = TextPrimary)
                Text(
                    if (owned) "owned" else "ambient sound",
                    fontFamily = Mono,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }

        if (owned) {
            StatePill("OWNED", GreenBright)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("\uD83E\uDE99", fontSize = 13.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    "${sound.price}",
                    fontFamily = Mono,
                    fontSize = 12.sp,
                    color = if (canAfford) AmberAccent else PriceRed
                )
            }
        }
    }
}

@Composable
private fun TomeSectionHeader(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text.uppercase(),
            fontFamily = Mono,
            fontSize = 10.sp,
            color = GreenBright,
            letterSpacing = 1.5.sp
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(BorderSubtle)
        )
    }
}

@Composable
private fun TomeRow(
    tome: Tome,
    owned: Int,
    canAfford: Boolean,
    lockedByLevel: Boolean,
    onBuy: () -> Unit
) {
    val context = LocalContext.current
    val resId = remember(tome.drawableName) {
        if (tome.drawableName.isBlank()) 0
        else context.resources.getIdentifier(tome.drawableName, "drawable", context.packageName)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .clickable(enabled = !lockedByLevel && canAfford, onClick = onBuy)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Surface2),
                contentAlignment = Alignment.Center
            ) {
                if (resId != 0) {
                    Image(
                        painter = painterResource(resId),
                        contentDescription = tome.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(tome.emoji, fontSize = 20.sp)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(tome.name, fontFamily = Mono, fontSize = 12.sp, color = TextPrimary, maxLines = 1)
                    if (owned > 0) {
                        Spacer(Modifier.width(6.dp))
                        Text("x$owned", fontFamily = Mono, fontSize = 11.sp, color = GreenBright)
                    }
                }
                Text(tome.desc, fontFamily = Mono, fontSize = 10.sp, color = TextMuted, maxLines = 1)
            }
        }
        Spacer(Modifier.width(8.dp))
        when {
            lockedByLevel -> Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, "Locked", tint = TextDim, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(4.dp))
                Text("Lv ${tome.requiredLevel}", fontFamily = Mono, fontSize = 10.sp, color = TextDim)
            }
            else -> Row(verticalAlignment = Alignment.CenterVertically) {
                Text("\uD83E\uDE99", fontSize = 13.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    "${tome.price}",
                    fontFamily = Mono,
                    fontSize = 12.sp,
                    color = if (canAfford) AmberAccent else PriceRed
                )
            }
        }
    }
}

@Composable
private fun GuardianItemRow(
    item: GuardianItem,
    owned: Int,
    canAfford: Boolean,
    magnetActive: Boolean,
    onBuy: () -> Unit,
    onActivate: () -> Unit
) {
    val context = LocalContext.current
    val resId = remember(item.drawableName) {
        context.resources.getIdentifier(item.drawableName, "drawable", context.packageName)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface1)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .clickable(enabled = canAfford, onClick = onBuy)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Surface2),
            contentAlignment = Alignment.Center
        ) {
            if (resId != 0) {
                Image(
                    painter = painterResource(resId),
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.name, fontFamily = Mono, fontSize = 12.sp, color = TextPrimary)
                Spacer(Modifier.width(6.dp))
                Text("x$owned", fontFamily = Mono, fontSize = 11.sp, color = GreenBright)
            }
            Text(
                item.description,
                fontFamily = Mono,
                fontSize = 9.sp,
                color = TextMuted,
                maxLines = 2
            )
            if (item.id == GuardianItemIds.LOOT_MAGNET && magnetActive) {
                Text("ACTIVE FOR NEXT VICTORY", fontFamily = Mono, fontSize = 9.sp, color = GreenBright)
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Surface2)
                    .padding(horizontal = 9.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("\uD83E\uDE99", fontSize = 11.sp)
                    Spacer(Modifier.width(3.dp))
                    Text(
                        "${item.price}",
                        fontFamily = Mono,
                        fontSize = 10.sp,
                        color = if (canAfford) AmberAccent else PriceRed
                    )
                }
            }
            if (item.id == GuardianItemIds.LOOT_MAGNET && owned > 0 && !magnetActive) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GreenDark)
                        .clickable(onClick = onActivate)
                        .padding(horizontal = 9.dp, vertical = 6.dp)
                ) {
                    Text("ACTIVATE", fontFamily = Mono, fontSize = 9.sp, color = GreenBright)
                }
            }
        }
    }
}

@Composable
private fun StatePill(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.18f))
            .border(0.5.dp, color, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, fontFamily = Mono, fontSize = 10.sp, color = color, letterSpacing = 1.sp)
    }
}
