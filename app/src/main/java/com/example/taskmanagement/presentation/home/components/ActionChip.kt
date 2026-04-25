package com.example.taskmanagement.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ActionChip(
    modifier: Modifier = Modifier,
    text: String,
    icon: @Composable () -> Unit,
    onClick:() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .defaultMinSize(minWidth = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Spacer(Modifier.width(8.dp))
            Text(text = text, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Preview
@Composable
private fun ActionChipPrev() {
    ActionChip(text = "Text 123", icon = {Icon(imageVector = Icons.Default.Add, contentDescription = null)})  {}
}