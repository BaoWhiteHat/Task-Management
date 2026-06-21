package com.example.taskmanagement.presentation.focus

data class AmbientSound(
    val id: String,
    val name: String,
    val price: Int,
    val rawResName: String
)

val ambientSounds = listOf(
    AmbientSound(id = "rain", name = "Rain", price = 0, rawResName = "rain"),
    AmbientSound(id = "forest", name = "Forest", price = 50, rawResName = "forest"),
    AmbientSound(id = "ocean", name = "Ocean", price = 150, rawResName = "ocean"),
)