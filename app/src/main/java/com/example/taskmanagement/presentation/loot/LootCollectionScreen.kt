package com.example.taskmanagement.presentation.loot

import com.example.taskmanagement.presentation.ui.theme.TaskTheme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
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
import androidx.compose.foundation.layout.width


private fun rarityColor(r: LootRarity): Color = when (r) {
    LootRarity.COMMON -> Color(0xFF9AA4AE)
    LootRarity.UNCOMMON -> Color(0xFF4ADE80)
    LootRarity.RARE -> Color(0xFF60A5FA)
    LootRarity.EPIC -> Color(0xFFC084FC)
}

private val rarityOrder = listOf(
    LootRarity.COMMON, LootRarity.UNCOMMON, LootRarity.RARE, LootRarity.EPIC
)

@Composable
fun LootCollectionButton(
    modifier: Modifier = Modifier,
    viewModel: LootViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.start(context) }
    val owned by viewModel.owned.collectAsState()
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
        ) { Text("\uD83C\uDF92", fontSize = 18.sp) }
        if (owned.isNotEmpty()) {
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
        LootCollectionDialog(viewModel = viewModel, onClose = { open = false })
    }
}

@Composable
fun LootCollectionDialog(viewModel: LootViewModel, onClose: () -> Unit) {
    val context = LocalContext.current
    val owned by viewModel.owned.collectAsState()
    val coins by viewModel.coins.collectAsState()
    var selected by remember { mutableStateOf<LootItem?>(null) }

    val foundCount = owned.size
    val dupValue = owned.entries.sumOf { (id, c) ->
        val extra = c - 1
        if (extra > 0) extra * (lootItemById(id)?.rarity?.sellPrice ?: 0) else 0
    }

    val grouped = remember { rarityOrder.map { tier -> tier to lootItems.filter { it.rarity == tier } } }

    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(BgDeep)
                .border(0.5.dp, GreenDark, RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

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
                        Text("SATCHEL", fontFamily = TaskTheme.fontFamily, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 2.sp)
                        Text("$foundCount / ${lootItems.size} collected", fontFamily = TaskTheme.fontFamily, fontSize = 10.sp, color = TextMuted)
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
                        Text("$coins", fontFamily = TaskTheme.fontFamily, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AmberAccent)
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    grouped.forEach { (tier, tierItems) ->
                        val ownedInTier = tierItems.count { (owned[it.id] ?: 0) > 0 }
                        item(span = { GridItemSpan(maxLineSpan) }, key = "h_${tier.name}") {
                            RaritySectionHeader(tier, ownedInTier, tierItems.size)
                        }
                        items(tierItems, key = { it.id }) { item ->
                            ItemCell(item = item, count = owned[item.id] ?: 0) { selected = item }
                        }
                    }
                }

                if (dupValue > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Surface1)
                            .border(0.5.dp, GreenDark, RoundedCornerShape(12.dp))
                            .clickable { viewModel.sellAllDuplicates(context) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Sell all duplicates  (+$dupValue \uD83E\uDE99)",
                            fontFamily = TaskTheme.fontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GreenBright
                        )
                    }
                }
            }
        }
    }

    val sel = selected
    if (sel != null) {
        val c = owned[sel.id] ?: 0
        ItemDetailDialog(
            item = sel,
            count = c,
            onSell = { viewModel.sellOne(context, sel.id) },
            onClose = { selected = null }
        )
        LaunchedEffect(c) { if (c == 0) selected = null }
    }
}

@Composable
private fun RaritySectionHeader(tier: LootRarity, owned: Int, total: Int) {
    val rc = rarityColor(tier)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(rc)
        )
        Spacer(Modifier.width(8.dp))
        Text(tier.label.uppercase(), fontFamily = TaskTheme.fontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = rc, letterSpacing = 2.sp)
        Spacer(Modifier.weight(1f))
        Text("$owned / $total", fontFamily = TaskTheme.fontFamily, fontSize = 10.sp, color = TextMuted)
    }
}

@Composable
private fun ItemCell(item: LootItem, count: Int, onClick: () -> Unit) {
    val owned = count > 0
    val context = LocalContext.current
    val resId = remember(item.drawableName) {
        context.resources.getIdentifier(item.drawableName, "drawable", context.packageName)
    }
    val rc = rarityColor(item.rarity)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(if (owned) Surface2 else Surface1.copy(alpha = 0.4f))
            .border(
                width = if (owned) 1.5.dp else 0.5.dp,
                color = if (owned) rc else BorderSubtle,
                shape = RoundedCornerShape(10.dp)
            )
            .then(if (owned) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (resId != 0) {
            Image(
                bitmap = ImageBitmap.imageResource(resId),
                contentDescription = if (owned) item.name else null,
                filterQuality = FilterQuality.None,
                colorFilter = if (owned) null else ColorFilter.tint(TextMuted.copy(alpha = 0.32f), BlendMode.SrcIn),
                modifier = Modifier.fillMaxSize(0.72f)
            )
        } else {
            Text("?", fontFamily = TaskTheme.fontFamily, fontSize = 20.sp, color = TextDim)
        }
        if (owned && count > 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(3.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(BgDeep.copy(alpha = 0.85f))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) { Text("\u00D7$count", fontFamily = TaskTheme.fontFamily, fontSize = 9.sp, color = TextPrimary) }
        }
    }
}

@Composable
private fun ItemDetailDialog(
    item: LootItem,
    count: Int,
    onSell: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val resId = remember(item.drawableName) {
        context.resources.getIdentifier(item.drawableName, "drawable", context.packageName)
    }
    val rc = rarityColor(item.rarity)

    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(BgDeep)
                .border(1.dp, rc, RoundedCornerShape(20.dp))
                .padding(22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Surface2)
                        .border(1.dp, rc.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (resId != 0) {
                        Image(
                            bitmap = ImageBitmap.imageResource(resId),
                            contentDescription = item.name,
                            filterQuality = FilterQuality.None,
                            modifier = Modifier.fillMaxSize(0.72f)
                        )
                    }
                }
                Text(item.name, fontFamily = TaskTheme.fontFamily, fontSize = 16.sp, color = TextPrimary)
                Text(
                    item.rarity.label.uppercase(),
                    fontFamily = TaskTheme.fontFamily, fontSize = 10.sp, color = rc, letterSpacing = 2.sp
                )
                Text(
                    "Owned \u00D7$count   \u00B7   Sell ${item.rarity.sellPrice} \uD83E\uDE99 each",
                    fontFamily = TaskTheme.fontFamily, fontSize = 11.sp, color = TextMuted
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GreenBright)
                        .clickable { onSell() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Sell one  (+${item.rarity.sellPrice})",
                        fontFamily = TaskTheme.fontFamily, fontSize = 13.sp, color = BgDeep
                    )
                }
                Text("Tap outside to close", fontFamily = TaskTheme.fontFamily, fontSize = 9.sp, color = TextDim)
            }
        }
    }
}