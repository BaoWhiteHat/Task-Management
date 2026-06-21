package com.example.taskmanagement.presentation.achievements

import com.example.taskmanagement.data.local.models.Achievement

object Metric {
    const val SESSIONS = "SESSIONS"
    const val MINUTES  = "MINUTES"
    const val TASKS    = "TASKS"
    const val STREAK   = "STREAK"
    const val LEVEL    = "LEVEL"
}

object Rarity {
    const val COMMON    = "Common"
    const val RARE      = "Rare"
    const val EPIC      = "Epic"
    const val LEGENDARY = "Legendary"
}

object Category {
    const val FOCUS  = "Focus"
    const val TASKS  = "Tasks"
    const val STREAK = "Streak"
    const val LEVEL  = "Level"
}

object AchievementDefs {

    val ALL: List<Achievement> = listOf(

        // FOCUS: session count
        Achievement(
            id = "focus_first", title = "First Focus",
            description = "Complete your first focus session.",
            iconName = "ach_01", category = Category.FOCUS, rarity = Rarity.COMMON,
            metric = Metric.SESSIONS, threshold = 1, sortOrder = 1
        ),
        Achievement(
            id = "focus_10", title = "Getting Focused",
            description = "Complete 10 focus sessions.",
            iconName = "ach_02", category = Category.FOCUS, rarity = Rarity.COMMON,
            metric = Metric.SESSIONS, threshold = 10, sortOrder = 2
        ),
        Achievement(
            id = "focus_50", title = "Focus Warrior",
            description = "Complete 50 focus sessions.",
            iconName = "ach_04", category = Category.FOCUS, rarity = Rarity.RARE,
            metric = Metric.SESSIONS, threshold = 50, sortOrder = 3
        ),
        Achievement(
            id = "focus_100", title = "Focus Master",
            description = "Complete 100 focus sessions.",
            iconName = "ach_06", category = Category.FOCUS, rarity = Rarity.EPIC,
            metric = Metric.SESSIONS, threshold = 100, sortOrder = 4
        ),

        // FOCUS: total minutes
        Achievement(
            id = "time_60", title = "Warming Up",
            description = "Focus for 60 minutes in total.",
            iconName = "ach_03", category = Category.FOCUS, rarity = Rarity.COMMON,
            metric = Metric.MINUTES, threshold = 60, sortOrder = 5
        ),
        Achievement(
            id = "time_300", title = "Deep Worker",
            description = "Focus for 300 minutes in total.",
            iconName = "ach_07", category = Category.FOCUS, rarity = Rarity.RARE,
            metric = Metric.MINUTES, threshold = 300, sortOrder = 6
        ),
        Achievement(
            id = "time_600", title = "In The Zone",
            description = "Focus for 600 minutes in total.",
            iconName = "ach_08", category = Category.FOCUS, rarity = Rarity.EPIC,
            metric = Metric.MINUTES, threshold = 600, sortOrder = 7
        ),
        Achievement(
            id = "time_1200", title = "Time Lord",
            description = "Focus for 1200 minutes in total.",
            iconName = "ach_10", category = Category.FOCUS, rarity = Rarity.LEGENDARY,
            metric = Metric.MINUTES, threshold = 1200, sortOrder = 8
        ),

        // TASKS: completed count
        Achievement(
            id = "task_first", title = "Task Beginner",
            description = "Complete your first task.",
            iconName = "ach_11", category = Category.TASKS, rarity = Rarity.COMMON,
            metric = Metric.TASKS, threshold = 1, sortOrder = 9
        ),
        Achievement(
            id = "task_10", title = "Task Doer",
            description = "Complete 10 tasks.",
            iconName = "ach_12", category = Category.TASKS, rarity = Rarity.COMMON,
            metric = Metric.TASKS, threshold = 10, sortOrder = 10
        ),
        Achievement(
            id = "task_25", title = "Task Crusher",
            description = "Complete 25 tasks.",
            iconName = "ach_14", category = Category.TASKS, rarity = Rarity.RARE,
            metric = Metric.TASKS, threshold = 25, sortOrder = 11
        ),
        Achievement(
            id = "task_50", title = "Task Champion",
            description = "Complete 50 tasks.",
            iconName = "ach_16", category = Category.TASKS, rarity = Rarity.EPIC,
            metric = Metric.TASKS, threshold = 50, sortOrder = 12
        ),
        Achievement(
            id = "task_100", title = "Task Legend",
            description = "Complete 100 tasks.",
            iconName = "ach_17", category = Category.TASKS, rarity = Rarity.LEGENDARY,
            metric = Metric.TASKS, threshold = 100, sortOrder = 13
        ),

        // STREAK: current daily streak
        Achievement(
            id = "streak_3", title = "On Fire",
            description = "Reach a 3-day focus streak.",
            iconName = "ach_21", category = Category.STREAK, rarity = Rarity.COMMON,
            metric = Metric.STREAK, threshold = 3, sortOrder = 14
        ),
        Achievement(
            id = "streak_7", title = "Unstoppable",
            description = "Reach a 7-day focus streak.",
            iconName = "ach_23", category = Category.STREAK, rarity = Rarity.RARE,
            metric = Metric.STREAK, threshold = 7, sortOrder = 15
        ),
        Achievement(
            id = "streak_30", title = "Iron Will",
            description = "Reach a 30-day focus streak.",
            iconName = "ach_30", category = Category.STREAK, rarity = Rarity.LEGENDARY,
            metric = Metric.STREAK, threshold = 30, sortOrder = 16
        ),

        // LEVEL: hero level
        Achievement(
            id = "level_5", title = "Growing Strong",
            description = "Reach level 5.",
            iconName = "ach_25", category = Category.LEVEL, rarity = Rarity.RARE,
            metric = Metric.LEVEL, threshold = 5, sortOrder = 17
        ),
        Achievement(
            id = "level_10", title = "Seasoned Hero",
            description = "Reach level 10.",
            iconName = "ach_28", category = Category.LEVEL, rarity = Rarity.EPIC,
            metric = Metric.LEVEL, threshold = 10, sortOrder = 18
        ),
    )
}