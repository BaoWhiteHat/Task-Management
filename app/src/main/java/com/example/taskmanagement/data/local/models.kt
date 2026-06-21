package com.example.taskmanagement.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val category: String,
    val rarity: String,
    val metric: String,
    val threshold: Int,
    val sortOrder: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0L
)