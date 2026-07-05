package com.example.taskmanagement.presentation.focus

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskmanagement.presentation.loot.LootItem
import com.example.taskmanagement.presentation.loot.LootRarity

private fun rarityColor(r: LootRarity): Color = when (r) {
    LootRarity.COMMON -> Color(0xFF9AA4AE)
    LootRarity.UNCOMMON -> Color(0xFF22C55E)
    LootRarity.RARE -> Color(0xFF3B82F6)
    LootRarity.EPIC -> Color(0xFF9333EA)
}

@Composable
fun BreakActivityDialog(
    suggestion: BreakActivitySuggestion,
    lootDrop: LootItem? = null,
    bonusLootDrop: LootItem? = null,
    onDismiss: () -> Unit,
    onAnotherIdea: () -> Unit,
    onStartBreak: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Break time ${suggestion.emoji}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (lootDrop != null) {
                    LootRewardCard(drop = lootDrop)
                }
                if (bonusLootDrop != null) {
                    LootRewardCard(drop = bonusLootDrop, bonus = true)
                }

                Text(
                    text = suggestion.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = suggestion.description
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onStartBreak
            ) {
                Text("Start break")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onAnotherIdea
            ) {
                Text("Another idea")
            }
        }
    )
}

// Loot chest shown on top of the break suggestion. Tap to open and reveal the drop.
@Composable
private fun LootRewardCard(drop: LootItem, bonus: Boolean = false) {
    val context = LocalContext.current
    var opened by remember(drop.id) { mutableStateOf(false) }
    val resId = remember(drop.drawableName) {
        context.resources.getIdentifier(drop.drawableName, "drawable", context.packageName)
    }
    val rc = rarityColor(drop.rarity)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(rc.copy(alpha = 0.10f))
            .border(1.dp, rc.copy(alpha = if (opened) 0.9f else 0.4f), RoundedCornerShape(14.dp))
            .then(if (opened) Modifier else Modifier.clickable { opened = true })
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(rc.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            if (opened && resId != 0) {
                Image(
                    bitmap = ImageBitmap.imageResource(resId),
                    contentDescription = drop.name,
                    filterQuality = FilterQuality.None,
                    modifier = Modifier.size(36.dp)
                )
            } else {
                Text("\uD83C\uDF81", fontSize = 24.sp)
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            if (opened) {
                Text(
                    text = drop.rarity.label.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = rc,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = drop.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

            } else {
                Text(
                    text = if (bonus) "Loot Magnet bonus!" else "You found loot!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Tap to open the chest",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
