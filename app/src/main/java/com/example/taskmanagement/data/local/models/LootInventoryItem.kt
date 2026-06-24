package com.example.taskmanagement.data.local.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "loot_inventory",
    foreignKeys = [
        ForeignKey(
            entity = GameProfile::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("profileId")]
)
data class LootInventoryItem(
    @PrimaryKey
    val itemId: String,
    val count: Int = 0,
    val profileId: Int = 1
)