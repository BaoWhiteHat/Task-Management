package com.example.taskmanagement.data.local.models

import androidx.room.Entity

@Entity(tableName = "quest_claim", primaryKeys = ["questId", "periodKey"])
data class QuestClaim(
    val questId: String,
    val periodKey: String
)