package com.example.taskmanagement.presentation.focus

import com.example.taskmanagement.presentation.ui.theme.TaskTheme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.taskmanagement.R


@Composable
fun LevelUpDialog(
    level: Int,
    title: String,
    onDismiss: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val colors = TaskTheme.colors
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(scheme.surface)
                .border(1.dp, scheme.primary.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "LEVEL UP!",
                fontFamily = TaskTheme.fontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = scheme.primary,
                letterSpacing = 4.sp
            )

            Image(
                painter = painterResource(id = R.drawable.ach_29),
                contentDescription = null,
                modifier = Modifier.size(110.dp)
            )

            Text(
                "Level $level",
                fontFamily = TaskTheme.fontFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = scheme.onSurface
            )
            Text(
                title,
                fontFamily = TaskTheme.fontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.accentColor,
                letterSpacing = 1.sp
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(scheme.primary)
                    .clickable { onDismiss() }
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Continue",
                    fontFamily = TaskTheme.fontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = scheme.onPrimary,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
