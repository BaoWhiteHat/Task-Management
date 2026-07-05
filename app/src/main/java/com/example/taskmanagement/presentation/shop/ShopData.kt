package com.example.taskmanagement.presentation.shop

data class ShopBackground(
    val id: String,
    val name: String,
    val price: Int,
    val requiredLevel: Int = 1
)

val shopBackgrounds = listOf(
    ShopBackground(id = "bg_02", name = "Ocean Cliffs",    price = 0,   requiredLevel = 1),
    ShopBackground(id = "bg_05", name = "Grassy Plains",   price = 0,   requiredLevel = 1),
    ShopBackground(id = "bg_01", name = "Autumn Forest",   price = 80,  requiredLevel = 2),
    ShopBackground(id = "bg_03", name = "Crystal River",   price = 80,  requiredLevel = 2),
    ShopBackground(id = "bg_07", name = "Golden Desert",   price = 100, requiredLevel = 3),
    ShopBackground(id = "bg_08", name = "Volcano Region",  price = 120, requiredLevel = 4),
    ShopBackground(id = "bg_11", name = "Floating Island", price = 150, requiredLevel = 5),
    ShopBackground(id = "bg_17", name = "Crystal Cavern",  price = 150, requiredLevel = 6),
    ShopBackground(id = "bg_20", name = "Frozen Throne",   price = 200, requiredLevel = 8),
    ShopBackground(id = "bg_19", name = "Volcanic Arena",  price = 200, requiredLevel = 8),
    ShopBackground(id = "bg_27", name = "Mage District",   price = 250, requiredLevel = 10),
    ShopBackground(id = "bg_16", name = "Abyssal Arena",   price = 250, requiredLevel = 12),
)

data class GuardianItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val drawableName: String
)

object GuardianItemIds {
    const val STREAK_SHIELD = "streak_shield"
    const val FOCUS_POTION = "focus_potion"
    const val TOME_SEAL = "tome_seal"
    const val LOOT_MAGNET = "loot_magnet"
    const val LOOT_MAGNET_ACTIVE = "loot_magnet_active"
}

val guardianItems = listOf(
    GuardianItem(
        id = GuardianItemIds.STREAK_SHIELD,
        name = "Streak Shield",
        description = "Protects your study streak from one missed day.",
        price = 250,
        drawableName = "ach_30"
    ),
    GuardianItem(
        id = GuardianItemIds.FOCUS_POTION,
        name = "Focus Potion",
        description = "Blocks one early retreat penalty.",
        price = 80,
        drawableName = "ach_04"
    ),
    GuardianItem(
        id = GuardianItemIds.TOME_SEAL,
        name = "Tome Seal",
        description = "Preserves one armed tome after victory.",
        price = 150,
        drawableName = "tome_02"
    ),
    GuardianItem(
        id = GuardianItemIds.LOOT_MAGNET,
        name = "Loot Magnet",
        description = "Adds one bonus loot roll to your next victory.",
        price = 150,
        drawableName = "ach_01"
    )
)
