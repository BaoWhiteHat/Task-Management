package com.example.taskmanagement.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_profile")
data class GameProfile(
    @PrimaryKey
    val id: Int = 1,
    val xp: Int = 0,
    val level: Int = 1,
    val coins: Int = 0,
    val totalSessions: Int = 0,
    val streakDays: Int = 0,
    val bestStreak: Int = 0,
    val lastFocusDate: String = "",
    val unlockedSounds: String = "rain"
) {
    val title: String
        get() = when {
            level >= 20 -> "Ancient Tree"
            level >= 15 -> "Mighty Oak"
            level >= 10 -> "Tree"
            level >= 6  -> "Sapling"
            level >= 3  -> "Sprout"
            else        -> "Seedling"
        }

    val xpForNextLevel: Int
        get() = 100 + (level - 1) * 50

    val xpProgress: Float
        get() = (xp.toFloat() / xpForNextLevel).coerceIn(0f, 1f)

    fun hasSound(soundId: String): Boolean {
        return unlockedSounds.split(",").contains(soundId)
    }
}