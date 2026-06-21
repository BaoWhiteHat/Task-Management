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