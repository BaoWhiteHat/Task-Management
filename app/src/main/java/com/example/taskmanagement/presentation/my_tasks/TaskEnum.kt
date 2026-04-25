package com.example.taskmanagement.presentation.my_tasks

import androidx.compose.ui.graphics.Color

enum class TaskTag{
    WORK,PERSONAL,HEALTH
}
enum class Priority(val color: Color){
    HIGH(color = Color(0xFFEF5350)),
    MEDIUM(color = Color(0xFFFFCA28)),
    LOW(color = Color(0xFF66BB6A))
}