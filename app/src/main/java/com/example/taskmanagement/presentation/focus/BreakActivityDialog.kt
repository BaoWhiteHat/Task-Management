package com.example.taskmanagement.presentation.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BreakActivityDialog(
    suggestion: BreakActivitySuggestion,
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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