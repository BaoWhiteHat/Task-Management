package com.example.taskmanagement.presentation.focus

data class BreakActivitySuggestion(
    val emoji: String,
    val title: String,
    val description: String
)

val breakActivitySuggestions = listOf(
    BreakActivitySuggestion(
        emoji = "🎧",
        title = "Listen to one song",
        description = "Play one relaxing song and rest your eyes for a few minutes."
    ),
    BreakActivitySuggestion(
        emoji = "🚶",
        title = "Take a short walk",
        description = "Stand up and walk around your room to refresh your body."
    ),
    BreakActivitySuggestion(
        emoji = "💧",
        title = "Drink water",
        description = "Grab a glass of water before your next focus session."
    ),
    BreakActivitySuggestion(
        emoji = "🧘",
        title = "Breathing reset",
        description = "Breathe in for 4 seconds, hold for 4, and breathe out slowly."
    ),
    BreakActivitySuggestion(
        emoji = "🧍",
        title = "Stretch your body",
        description = "Stretch your neck, shoulders, and back to reduce tension."
    ),
    BreakActivitySuggestion(
        emoji = "📝",
        title = "Quick reflection",
        description = "Write down one thing you completed in this focus session."
    )
)