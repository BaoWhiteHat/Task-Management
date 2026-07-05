package com.example.taskmanagement.data.local.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "profile_sounds",
    primaryKeys = ["profileId", "soundId"],
    foreignKeys = [
        ForeignKey(
            entity = GameProfile::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["profileId"])]
)
data class ProfileSound(
    val profileId: Int,
    val soundId: String
)
