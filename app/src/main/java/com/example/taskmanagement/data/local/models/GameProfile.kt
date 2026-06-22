package com.example.taskmanagement.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_profile")
data class GameProfile(
    @PrimaryKey
    val id: Int = 1,
    val xp: Int = 0,
    val level: Int = 10,
    val coins: Int = 1000,
    val totalSessions: Int = 0,
    val streakDays: Int = 0,
    val bestStreak: Int = 0,
    val lastFocusDate: String = "",
    val unlockedSounds: String = "rain",
    val unlockedBackgrounds: String = "",
    val selectedBackgroundId: String = "",
    val tomeInventory: String = "",
    val lastLoginDate: String = "",
    val loginStreak: Int = 0


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

    fun hasBackground(backgroundId: String): Boolean {
        return unlockedBackgrounds.split(",").contains(backgroundId)
    }

    fun tomeCount(tomeId: String): Int {
        if (tomeInventory.isBlank()) return 0
        for (part in tomeInventory.split(",")) {
            val kv = part.split(":")
            if (kv.size == 2 && kv[0] == tomeId) return kv[1].toIntOrNull() ?: 0
        }
        return 0
    }
}