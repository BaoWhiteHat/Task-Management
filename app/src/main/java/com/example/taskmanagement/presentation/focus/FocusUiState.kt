package com.example.taskmanagement.presentation.focus

import com.example.taskmanagement.data.local.models.GameProfile
import com.example.taskmanagement.presentation.loot.LootItem

enum class FocusPhase {
    STUDY,
    BREAK
}

data class FocusPreset(
    val title: String,
    val description: String,
    val studyMinutes: Int,
    val breakMinutes: Int,
    val xpReward: Int,
    val coinReward: Int
) {
    val totalMinutes: Int get() = studyMinutes + breakMinutes
    val studySeconds: Int get() = studyMinutes * 60
    val breakSeconds: Int get() = breakMinutes * 60
}

val focusPresets = listOf(
    FocusPreset(
        title = "30 min",
        description = "25 min study + 5 min break",
        studyMinutes = 25,
        breakMinutes = 5,
        xpReward = 30,
        coinReward = 10
    ),
    FocusPreset(
        title = "60 min",
        description = "50 min study + 10 min break",
        studyMinutes = 50,
        breakMinutes = 10,
        xpReward = 60,
        coinReward = 20
    )
)

data class LevelUpInfo(
    val level: Int,
    val title: String
)

data class FocusUiState(
    val selectedPresetIndex: Int = 0,
    val timeLeft: Int = focusPresets[0].studySeconds,
    val isRunning: Boolean = false,
    val phase: FocusPhase = FocusPhase.STUDY,
    val completedStudySessions: Int = 0,

    // Game
    val gameProfile: GameProfile? = null,
    val selectedSoundId: String? = "rain",
    val tomeCounts: Map<String, Int> = emptyMap(),
    val armedTomeId: String? = null,
    val showPenaltyWarning: Boolean = false,

    val showSessionCompletePopup: Boolean = false,

    val showBreakActivityPopup: Boolean = false,
    val breakActivitySuggestion: BreakActivitySuggestion? = null,
    val lootDrop: LootItem? = null,
    val levelUp: LevelUpInfo? = null

) {
    val selectedPreset: FocusPreset
        get() = focusPresets[selectedPresetIndex]

    val isBreak: Boolean
        get() = phase == FocusPhase.BREAK

    val phaseTitle: String
        get() = if (isBreak) "Break Time" else "Study Time"

    val phaseSubtitle: String
        get() = if (isBreak) "Take a short rest before your next focus round."
        else "Stay focused. Your break is coming soon."

    val currentPhaseTotalSeconds: Int
        get() = if (isBreak) selectedPreset.breakSeconds else selectedPreset.studySeconds

    val remainingProgress: Float
        get() = if (currentPhaseTotalSeconds == 0) 0f
        else (timeLeft.toFloat() / currentPhaseTotalSeconds).coerceIn(0f, 1f)

    val elapsedProgress: Float
        get() = (1f - remainingProgress).coerceIn(0f, 1f)
}
