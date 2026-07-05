package com.example.taskmanagement.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_profile")
data class GameProfile(
    @PrimaryKey
    val id: Int = 1,
    val xp: Int = 0,
    val level: Int = 3,
    val coins: Int = 10000,
    val totalSessions: Int = 0,
    val streakDays: Int = 0,
    val bestStreak: Int = 0,
    val lastFocusDate: String = "",
    @Deprecated("Sound ownership is stored in profile_sounds")
    val unlockedSounds: String = "rain",
    @Deprecated("Background ownership is stored in profile_backgrounds")
    val unlockedBackgrounds: String = "",
    val selectedBackgroundId: String = "",
    // Legacy prototype column. Active tome inventory is stored in profile_tomes.
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

    @Deprecated("Use ProfileSoundDao.isUnlocked")
    fun hasSound(soundId: String): Boolean {
        return unlockedSounds.split(",").contains(soundId)
    }

    @Deprecated("Use ProfileBackgroundDao.isUnlocked")
    fun hasBackground(backgroundId: String): Boolean {
        return unlockedBackgrounds.split(",").contains(backgroundId)
    }

}
