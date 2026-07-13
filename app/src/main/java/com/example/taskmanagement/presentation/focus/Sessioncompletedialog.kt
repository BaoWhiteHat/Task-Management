package com.example.taskmanagement.presentation.focus

import com.example.taskmanagement.presentation.ui.theme.TaskTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SessionCompleteDialog(
    onContinue: () -> Unit,
    onLeave: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val colors = TaskTheme.colors
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
                .background(scheme.surface)
                .border(0.5.dp, scheme.primary, RoundedCornerShape(18.dp))
                .padding(22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("\uD83C\uDF33", fontSize = 42.sp)
                Text(
                    "Round complete!",
                    fontFamily = TaskTheme.fontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = scheme.onSurface
                )
                Text(
                    "Your tree joined the forest. Fight again or head out?",
                    fontFamily = TaskTheme.fontFamily,
                    fontSize = 11.sp,
                    color = colors.subText
                )

                // Fight again (primary)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(scheme.primary)
                        .clickable(onClick = onContinue)
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "\u2694 Fight again",
                        fontFamily = TaskTheme.fontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = scheme.onPrimary
                    )
                }

                // Leave (ghost)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(scheme.surfaceVariant)
                        .border(0.5.dp, scheme.outline, RoundedCornerShape(12.dp))
                        .clickable(onClick = onLeave)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Leave", fontFamily = TaskTheme.fontFamily, fontSize = 12.sp, color = colors.subText)
                }
            }
        }
    }
}
