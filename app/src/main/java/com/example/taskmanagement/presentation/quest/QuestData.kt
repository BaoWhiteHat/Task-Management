package com.example.taskmanagement.presentation.quest

import com.example.taskmanagement.presentation.loot.LootRarity
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.IsoFields

enum class QuestPeriod { DAILY, WEEKLY }
enum class QuestMetric { SESSIONS, MINUTES }

data class Quest(
    val id: String,
    val period: QuestPeriod,
    val metric: QuestMetric,
    val target: Int,
    val title: String,
    val coins: Int,
    val rewardRarity: LootRarity? = null,
    val iconDrawable: String = ""
)
val quests: List<Quest> = listOf(
    // Daily
    Quest("d_sessions_2", QuestPeriod.DAILY, QuestMetric.SESSIONS, 2,
        "Win 2 focus battles today", coins = 20, rewardRarity = LootRarity.COMMON, iconDrawable = "tome_01"),
    Quest("d_sessions_4", QuestPeriod.DAILY, QuestMetric.SESSIONS, 4,
        "Win 4 focus battles today", coins = 35, rewardRarity = LootRarity.UNCOMMON, iconDrawable = "tome_03"),
    Quest("d_minutes_90", QuestPeriod.DAILY, QuestMetric.MINUTES, 90,
        "Study 90 minutes today", coins = 50, rewardRarity = LootRarity.RARE, iconDrawable = "tome_08"),

    // Weekly
    Quest("w_sessions_12", QuestPeriod.WEEKLY, QuestMetric.SESSIONS, 12,
        "Win 12 focus battles this week", coins = 120, rewardRarity = LootRarity.UNCOMMON, iconDrawable = "tome_09"),
    Quest("w_minutes_400", QuestPeriod.WEEKLY, QuestMetric.MINUTES, 400,
        "Study 400 minutes this week", coins = 180, rewardRarity = LootRarity.RARE, iconDrawable = "tome_11"),
    Quest("w_sessions_25", QuestPeriod.WEEKLY, QuestMetric.SESSIONS, 25,
        "Win 25 focus battles this week", coins = 300, rewardRarity = LootRarity.EPIC, iconDrawable = "tome_13")
)

fun periodKey(period: QuestPeriod, date: LocalDate = LocalDate.now()): String = when (period) {
    QuestPeriod.DAILY -> date.toString()                                   // e.g. 2026-06-22
    QuestPeriod.WEEKLY -> {
        val week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
        val year = date.get(IsoFields.WEEK_BASED_YEAR)
        "%d-W%02d".format(year, week)                                      // e.g. 2026-W26
    }
}

data class TrackBonus(
    val id: String,
    val period: QuestPeriod,
    val coins: Int,
    val rewardRarity: LootRarity
)

val trackBonuses: List<TrackBonus> = listOf(
    TrackBonus("d_track_bonus", QuestPeriod.DAILY, 80, LootRarity.RARE),
    TrackBonus("w_track_bonus", QuestPeriod.WEEKLY, 250, LootRarity.EPIC)
)

fun trackBonusFor(period: QuestPeriod): TrackBonus = trackBonuses.first { it.period == period }

fun questsFor(period: QuestPeriod): List<Quest> = quests.filter { it.period == period }

fun resetLabel(period: QuestPeriod, now: LocalDateTime = LocalDateTime.now()): String {
    val target = when (period) {
        QuestPeriod.DAILY -> now.toLocalDate().plusDays(1).atStartOfDay()
        QuestPeriod.WEEKLY -> {
            val diff = ((DayOfWeek.MONDAY.value - now.dayOfWeek.value) + 7) % 7
            val days = if (diff == 0) 7 else diff
            now.toLocalDate().plusDays(days.toLong()).atStartOfDay()
        }
    }
    val totalMin = Duration.between(now, target).toMinutes().coerceAtLeast(0)
    val days = totalMin / (60 * 24)
    val hours = (totalMin / 60) % 24
    val minutes = totalMin % 60
    return if (days > 0) "${days}d ${hours}h" else "${hours}h ${minutes}m"
}