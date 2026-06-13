package com.example.taskmanagement.presentation.focus

import androidx.compose.ui.graphics.Color

enum class EnemyType(
    val displayName: String,
    val sprite: String,
    val accent: Color,
    val panelBorder: Color
) {
    DEADLINE_DEMON("Deadline Demon", "\uD83D\uDC79", Color(0xFFD49BE8), Color(0xFF6E3A72)),
    KNOWLEDGE_SPHINX("Knowledge Sphinx", "\uD83E\uDD81", Color(0xFF85B7EB), Color(0xFF2E5A8E)),
    LAZY_SLIME("Lazy Slime", "\uD83D\uDFE2", Color(0xFF86D72F), Color(0xFF4F7A1F)),
    CHAOS_IMP("Chaos Imp", "\uD83D\uDC7A", Color(0xFFFFB020), Color(0xFF8E5A1F)),
    WILD_SLIME("Wild Slime", "\uD83C\uDF00", Color(0xFFA8BB8E), Color(0xFF2D4A1E));
}

enum class EnemyRank(
    val label: String,
    val xpMultiplier: Float,
    val coinMultiplier: Float
) {
    MINION("Minion", 0.7f, 0.7f),
    NORMAL("Mini Boss", 1f, 1f),
    BOSS("Boss", 2f, 2f)
}

data class Encounter(
    val title: String,
    val type: EnemyType,
    val rank: EnemyRank
) {
    val enemyName: String get() = type.displayName
    val isBoss: Boolean get() = rank == EnemyRank.BOSS
}

fun encounterFromTask(
    taskTitle: String?,
    tag: String?,
    priority: String?
): Encounter {
    val type = when (tag?.trim()?.lowercase()) {
        "work" -> EnemyType.DEADLINE_DEMON
        "study" -> EnemyType.KNOWLEDGE_SPHINX
        "health" -> EnemyType.LAZY_SLIME
        "personal" -> EnemyType.CHAOS_IMP
        else -> EnemyType.WILD_SLIME
    }
    val rank = when (priority?.trim()?.lowercase()) {
        "high" -> EnemyRank.BOSS
        "low" -> EnemyRank.MINION
        else -> EnemyRank.NORMAL
    }
    val title = taskTitle?.takeIf { it.isNotBlank() } ?: "Free Focus"
    return Encounter(title = title, type = type, rank = rank)
}