package com.example.taskmanagement.presentation.focus

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.taskmanagement.R

private val Pixel = FontFamily.Monospace

@Composable
fun LevelUpDialog(
    level: Int,
    title: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(BgDeep)
                .border(1.dp, GreenBright.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "LEVEL UP!",
                fontFamily = Pixel,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GreenBright,
                letterSpacing = 4.sp
            )

            Image(
                painter = painterResource(id = R.drawable.ach_29),
                contentDescription = null,
                modifier = Modifier.size(110.dp)
            )

            Text(
                "Level $level",
                fontFamily = Pixel,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                title,
                fontFamily = Pixel,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AmberAccent,
                letterSpacing = 1.sp
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(GreenBright)
                    .clickable { onDismiss() }
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Continue",
                    fontFamily = Pixel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BgDeep,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}