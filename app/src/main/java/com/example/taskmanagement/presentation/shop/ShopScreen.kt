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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanagement.presentation.focus.AmbientSound
import com.example.taskmanagement.presentation.focus.ambientSounds
import com.example.taskmanagement.presentation.ui.theme.TaskTheme


private data class ShopPalette(
    val background: Color,
    val cardSurface: Color,
    val insetSurface: Color,
    val border: Color,
    val selectedBorder: Color,
    val selectedFill: Color,
    val titleText: Color,
    val mutedText: Color,
    val dimText: Color,
    val accentText: Color,
    val priceText: Color,
    val unavailableText: Color,
    val successText: Color,
    val successFill: Color,
    val warningText: Color,
    val warningFill: Color,
)

@Composable
private fun shopPalette(): ShopPalette {
    val scheme = MaterialTheme.colorScheme
    val taskColors = TaskTheme.colors
    return ShopPalette(
        background = scheme.background,
        cardSurface = taskColors.cardBg,
        insetSurface = scheme.surfaceVariant,
        border = scheme.outline,
        selectedBorder = scheme.primary,
        selectedFill = scheme.primary.copy(alpha = 0.18f),
        titleText = scheme.onSurface,
        mutedText = scheme.onSurfaceVariant,
        dimText = taskColors.subText,
        accentText = taskColors.successText,
        priceText = taskColors.priorityMedium,
        unavailableText = scheme.error,
        successText = taskColors.successText,
        successFill = taskColors.successBg,
        warningText = taskColors.priorityMedium,
        warningFill = taskColors.priorityMediumBg,
    )
}

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
    val colors = shopPalette()

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
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
                        .background(colors.cardSurface)
                        .border(0.5.dp, colors.border, CircleShape)
                        .clickable(onClick = onNavigateBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, "Back", tint = colors.titleText)
                }

                Text(
                    "SHOP",
                    fontFamily = TaskTheme.fontFamily,
                    fontSize = 13.sp,
                    color = colors.accentText,
                    letterSpacing = 2.sp
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.cardSurface)
                        .border(0.5.dp, colors.border, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("\uD83E\uDE99", fontSize = 13.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "$coins",
                        fontFamily = TaskTheme.fontFamily,
                        fontSize = 12.sp,
                        color = colors.priceText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.cardSurface)
                    .border(0.5.dp, colors.border, RoundedCornerShape(20.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                ShopTab("SCENES", tab == "bg", Modifier.weight(1f)) { tab = "bg" }
                ShopTab("SOUNDS", tab == "sound", Modifier.weight(1f)) { tab = "sound" }
                ShopTab("TOMES", tab == "tome", Modifier.weight(1f)) { tab = "tome" }
                ShopTab("ITEMS", tab == "items", Modifier.weight(1f)) { tab = "items" }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (tab) {
                    "bg" -> BackgroundsTab(
                        selectedBg = selectedBg,
                        unlockedBackgroundIds = unlockedBackgroundIds,
                        coins = coins,
                        level = profile?.level ?: 1,
                        onDefaultSelected = { viewModel.equipBackground(context, "") },
                        onBuy = { viewModel.buyBackground(context, it) },
                        onEquip = { viewModel.equipBackground(context, it) }
                    )

                    "sound" -> SoundsTab(
                        unlockedSoundIds = unlockedSoundIds,
                        coins = coins,
                        onBuy = { viewModel.buySound(context, it) }
                    )

                    "tome" -> TomesTab(
                        tomeCounts = tomeCounts,
                        coins = coins,
                        level = profile?.level ?: 1,
                        onBuy = { viewModel.buyTome(context, it) }
                    )

                    else -> GuardianItemsTab(
                        guardianItemCounts = guardianItemCounts,
                        coins = coins,
                        onBuy = { viewModel.buyGuardianItem(context, it) },
                        onActivateLootMagnet = { viewModel.activateLootMagnet(context) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BackgroundsTab(
    selectedBg: String,
    unlockedBackgroundIds: Set<String>,
    coins: Int,
    level: Int,
    onDefaultSelected: () -> Unit,
    onBuy: (ShopBackground) -> Unit,
    onEquip: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DefaultBgRow(
            selected = selectedBg.isBlank(),
            onClick = onDefaultSelected
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
                        lockedByLevel = !owned && level < bg.requiredLevel,
                        modifier = Modifier.weight(1f),
                        onBuy = { onBuy(bg) },
                        onEquip = { onEquip(bg.id) }
                    )
                }
                repeat(2 - rowItems.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SoundsTab(
    unlockedSoundIds: Set<String>,
    coins: Int,
    onBuy: (AmbientSound) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ambientSounds.forEach { sound ->
            SoundRow(
                sound = sound,
                owned = sound.id in unlockedSoundIds,
                canAfford = coins >= sound.price,
                onBuy = { onBuy(sound) }
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun TomesTab(
    tomeCounts: Map<String, Int>,
    coins: Int,
    level: Int,
    onBuy: (Tome) -> Unit
) {
    val colors = shopPalette()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            "Consumable - used once before a battle. The buff applies when you win.",
            fontFamily = TaskTheme.fontFamily,
            fontSize = 10.sp,
            color = colors.dimText
        )
        shopTomes.groupBy { it.category }.forEach { (category, tomes) ->
            TomeSectionHeader(category)
            tomes.forEach { tome ->
                TomeRow(
                    tome = tome,
                    owned = tomeCounts[tome.id] ?: 0,
                    canAfford = coins >= tome.price,
                    lockedByLevel = level < tome.requiredLevel,
                    onBuy = { onBuy(tome) }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun GuardianItemsTab(
    guardianItemCounts: Map<String, Int>,
    coins: Int,
    onBuy: (GuardianItem) -> Unit,
    onActivateLootMagnet: () -> Unit
) {
    val colors = shopPalette()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            "Guardian Items protect your progress and strengthen future victories.",
            fontFamily = TaskTheme.fontFamily,
            fontSize = 10.sp,
            color = colors.dimText
        )
        guardianItems.forEach { item ->
            GuardianItemRow(
                item = item,
                owned = guardianItemCounts[item.id] ?: 0,
                canAfford = coins >= item.price,
                magnetActive = (guardianItemCounts[GuardianItemIds.LOOT_MAGNET_ACTIVE] ?: 0) > 0,
                onBuy = { onBuy(item) },
                onActivate = onActivateLootMagnet
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ShopTab(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val colors = shopPalette()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) colors.selectedFill else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontFamily = TaskTheme.fontFamily,
            fontSize = 11.sp,
            color = if (selected) colors.accentText else colors.mutedText,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun DefaultBgRow(selected: Boolean, onClick: () -> Unit) {
    val colors = shopPalette()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.cardSurface)
            .border(
                if (selected) 1.5.dp else 0.5.dp,
                if (selected) colors.selectedBorder else colors.border,
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
                Text("Default", fontFamily = TaskTheme.fontFamily, fontSize = 13.sp, color = colors.titleText)
                Text("Day / night sky", fontFamily = TaskTheme.fontFamily, fontSize = 10.sp, color = colors.mutedText)
            }
        }
        if (selected) {
            StatePill("ACTIVE", colors.successText, colors.successFill)
        } else {
            StatePill("USE", colors.warningText, colors.warningFill)
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
    val colors = shopPalette()
    val context = LocalContext.current
    val resId = remember(bg.id) {
        context.resources.getIdentifier(bg.id, "drawable", context.packageName)
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.cardSurface)
            .border(
                width = if (selected) 1.5.dp else 0.5.dp,
                color = if (selected) colors.selectedBorder else colors.border,
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
                .background(colors.insetSurface),
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
                Text("?", fontFamily = TaskTheme.fontFamily, color = colors.dimText, fontSize = 26.sp)
            }
            if (lockedByLevel) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = colors.titleText,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Text(
            bg.name,
            fontFamily = TaskTheme.fontFamily,
            fontSize = 11.sp,
            color = colors.titleText,
            maxLines = 1
        )

        when {
            selected -> StatePill("ACTIVE", colors.successText, colors.successFill)
            owned -> StatePill("USE", colors.warningText, colors.warningFill)
            lockedByLevel -> Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, "Locked", tint = colors.dimText, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    "Reach Lv ${bg.requiredLevel}",
                    fontFamily = TaskTheme.fontFamily,
                    fontSize = 10.sp,
                    color = colors.dimText
                )
            }
            else -> Row(verticalAlignment = Alignment.CenterVertically) {
                Text("\uD83E\uDE99", fontSize = 12.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    "${bg.price}",
                    fontFamily = TaskTheme.fontFamily,
                    fontSize = 12.sp,
                    color = if (canAfford) colors.priceText else colors.unavailableText
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    if (canAfford) "buy" else "need more",
                    fontFamily = TaskTheme.fontFamily,
                    fontSize = 9.sp,
                    color = colors.dimText
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
    val colors = shopPalette()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.cardSurface)
            .border(0.5.dp, colors.border, RoundedCornerShape(12.dp))
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
                    .background(colors.insetSurface),
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
                    Icon(Icons.Default.Lock, "Locked", tint = colors.mutedText, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(sound.name, fontFamily = TaskTheme.fontFamily, fontSize = 13.sp, color = colors.titleText)
                Text(
                    if (owned) "owned" else "ambient sound",
                    fontFamily = TaskTheme.fontFamily,
                    fontSize = 10.sp,
                    color = colors.mutedText
                )
            }
        }

        if (owned) {
            StatePill("OWNED", colors.successText, colors.successFill)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("\uD83E\uDE99", fontSize = 13.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    "${sound.price}",
                    fontFamily = TaskTheme.fontFamily,
                    fontSize = 12.sp,
                    color = if (canAfford) colors.priceText else colors.unavailableText
                )
            }
        }
    }
}

@Composable
private fun TomeSectionHeader(text: String) {
    val colors = shopPalette()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text.uppercase(),
            fontFamily = TaskTheme.fontFamily,
            fontSize = 10.sp,
            color = colors.accentText,
            letterSpacing = 1.5.sp
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(colors.border)
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
    val colors = shopPalette()
    val context = LocalContext.current
    val resId = remember(tome.drawableName) {
        if (tome.drawableName.isBlank()) 0
        else context.resources.getIdentifier(tome.drawableName, "drawable", context.packageName)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.cardSurface)
            .border(0.5.dp, colors.border, RoundedCornerShape(12.dp))
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
                    .background(colors.insetSurface),
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
                    Text(tome.name, fontFamily = TaskTheme.fontFamily, fontSize = 12.sp, color = colors.titleText, maxLines = 1)
                    if (owned > 0) {
                        Spacer(Modifier.width(6.dp))
                        Text("x$owned", fontFamily = TaskTheme.fontFamily, fontSize = 11.sp, color = colors.successText)
                    }
                }
                Text(tome.desc, fontFamily = TaskTheme.fontFamily, fontSize = 10.sp, color = colors.mutedText, maxLines = 1)
            }
        }
        Spacer(Modifier.width(8.dp))
        when {
            lockedByLevel -> Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, "Locked", tint = colors.dimText, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(4.dp))
                Text("Lv ${tome.requiredLevel}", fontFamily = TaskTheme.fontFamily, fontSize = 10.sp, color = colors.dimText)
            }
            else -> Row(verticalAlignment = Alignment.CenterVertically) {
                Text("\uD83E\uDE99", fontSize = 13.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    "${tome.price}",
                    fontFamily = TaskTheme.fontFamily,
                    fontSize = 12.sp,
                    color = if (canAfford) colors.priceText else colors.unavailableText
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
    val colors = shopPalette()
    val context = LocalContext.current
    val resId = remember(item.drawableName) {
        context.resources.getIdentifier(item.drawableName, "drawable", context.packageName)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.cardSurface)
            .border(0.5.dp, colors.border, RoundedCornerShape(12.dp))
            .clickable(enabled = canAfford, onClick = onBuy)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.insetSurface),
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
                Text(item.name, fontFamily = TaskTheme.fontFamily, fontSize = 12.sp, color = colors.titleText)
                Spacer(Modifier.width(6.dp))
                Text("x$owned", fontFamily = TaskTheme.fontFamily, fontSize = 11.sp, color = colors.successText)
            }
            Text(
                item.description,
                fontFamily = TaskTheme.fontFamily,
                fontSize = 9.sp,
                color = colors.mutedText,
                maxLines = 2
            )
            if (item.id == GuardianItemIds.LOOT_MAGNET && magnetActive) {
                Text("ACTIVE FOR NEXT VICTORY", fontFamily = TaskTheme.fontFamily, fontSize = 9.sp, color = colors.successText)
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.insetSurface)
                    .padding(horizontal = 9.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("\uD83E\uDE99", fontSize = 11.sp)
                    Spacer(Modifier.width(3.dp))
                    Text(
                        "${item.price}",
                        fontFamily = TaskTheme.fontFamily,
                        fontSize = 10.sp,
                        color = if (canAfford) colors.priceText else colors.unavailableText
                    )
                }
            }
            if (item.id == GuardianItemIds.LOOT_MAGNET && owned > 0 && !magnetActive) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.selectedFill)
                        .border(0.5.dp, colors.selectedBorder, RoundedCornerShape(8.dp))
                        .clickable(onClick = onActivate)
                        .padding(horizontal = 9.dp, vertical = 6.dp)
                ) {
                    Text("ACTIVATE", fontFamily = TaskTheme.fontFamily, fontSize = 9.sp, color = colors.accentText)
                }
            }
        }
    }
}

@Composable
private fun StatePill(
    text: String,
    contentColor: Color,
    containerColor: Color = contentColor.copy(alpha = 0.18f)
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .border(0.5.dp, contentColor.copy(alpha = 0.72f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, fontFamily = TaskTheme.fontFamily, fontSize = 10.sp, color = contentColor, letterSpacing = 1.sp)
    }
}
