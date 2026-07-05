package com.example.taskmanagement.presentation.focus

data class AmbientSound(
    val id: String,
    val name: String,
    val price: Int,
    val rawResName: String,
    val icon: String
)

val ambientSounds = listOf(
    AmbientSound(
        id = "rain",
        name = "Rain",
        price = 0,
        rawResName = "rain",
        icon = "\uD83C\uDF27"
    ),
    AmbientSound(
        id = "forest",
        name = "Forest",
        price = 50,
        rawResName = "forest",
        icon = "\uD83C\uDF32"
    ),
    AmbientSound(
        id = "ocean",
        name = "Ocean",
        price = 150,
        rawResName = "ocean",
        icon = "\uD83C\uDF0A"
    ),
)

fun ambientSoundById(soundId: String): AmbientSound? =
    ambientSounds.firstOrNull { it.id == soundId }
