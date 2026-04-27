package com.example.taskmanagement.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = { Text("Today") },
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTaskTopAppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = { Text("My tasks") },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopAppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = { Text("Calendar") },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsTopAppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = { Text("Analytics") },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskTopAppBar(modifier: Modifier = Modifier, onNavigateBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("New Task") },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Filled.Close, contentDescription = "close")
            }
        }
    )
}