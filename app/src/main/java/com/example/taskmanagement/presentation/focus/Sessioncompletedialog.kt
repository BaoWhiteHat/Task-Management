package com.example.taskmanagement.presentation.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SessionCompleteDialog(
    onContinue: () -> Unit,
    onLeave: () -> Unit
) {
    val mono = FontFamily.Monospace

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(28.dp)
                .widthIn(max = 340.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Surface1)
                .border(0.5.dp, GreenBright, RoundedCornerShape(18.dp))
                .padding(22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("\uD83C\uDF33", fontSize = 42.sp)
                Text(
                    "Round complete!",
                    fontFamily = mono,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    "Your tree joined the forest. Fight again or head out?",
                    fontFamily = mono,
                    fontSize = 11.sp,
                    color = TextMuted
                )

                // Fight again (primary)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GreenBright)
                        .clickable(onClick = onContinue)
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "\u2694 Fight again",
                        fontFamily = mono,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = BgDeep
                    )
                }

                // Leave (ghost)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Surface2)
                        .border(0.5.dp, BorderSubtle, RoundedCornerShape(12.dp))
                        .clickable(onClick = onLeave)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Leave", fontFamily = mono, fontSize = 12.sp, color = TextMuted)
                }
            }
        }
    }
}