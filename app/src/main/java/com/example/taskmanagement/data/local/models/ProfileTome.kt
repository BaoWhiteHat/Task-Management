package com.example.taskmanagement.data.local.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "profile_tomes",
    primaryKeys = ["profileId", "tomeId"],
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
data class ProfileTome(
    val profileId: Int,
    val tomeId: String,
    val count: Int
)
