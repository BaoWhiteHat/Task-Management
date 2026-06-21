package com.example.taskmanagement.presentation.achievements

import android.content.Context
import com.example.taskmanagement.data.local.AppDatabase
import com.example.taskmanagement.data.local.models.Achievement
import com.example.taskmanagement.data.local.models.GameProfile
import kotlinx.coroutines.flow.first

object AchievementTracker {

    suspend fun ensureSeeded(context: Context) {
        val dao = AppDatabase.getDatabase(context.applicationContext).achievementDao()
        if (dao.count() == 0) {
            dao.insertAll(AchievementDefs.ALL)
        }
    }

    // Current value of every tracked stat. Used for the Journey progress bars.
    suspend fun currentStats(context: Context): Map<String, Int> {
        val db = AppDatabase.getDatabase(context.applicationContext)
        val profile: GameProfile = db.gameProfileDao().getProfile().first() ?: GameProfile()
        val sessions = db.focusSessionDao().getAllFocusSessions().first()
        val tasksDone = db.taskDao().getCompletedCount().first()
        return mapOf(
            Metric.SESSIONS to sessions.size,
            Metric.MINUTES  to sessions.sumOf { it.studyMinutes },
            Metric.TASKS    to tasksDone,
            Metric.STREAK   to profile.streakDays,
            Metric.LEVEL    to profile.level
        )
    }

    suspend fun checkAndUnlock(context: Context): List<Achievement> {
        val db = AppDatabase.getDatabase(context.applicationContext)
        ensureSeeded(context)

        val current = currentStats(context)
        val now = System.currentTimeMillis()
        val newlyUnlocked = mutableListOf<Achievement>()

        for (a in db.achievementDao().getAllOnce()) {
            if (!a.isUnlocked && (current[a.metric] ?: 0) >= a.threshold) {
                db.achievementDao().unlock(a.id, now)
                newlyUnlocked.add(a.copy(isUnlocked = true, unlockedAt = now))
            }
        }
        return newlyUnlocked
    }
}