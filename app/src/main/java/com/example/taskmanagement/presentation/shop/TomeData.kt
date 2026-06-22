package com.example.taskmanagement.presentation.shop

data class Tome(
    val id: String,
    val name: String,
    val desc: String,
    val price: Int,
    val category: String,
    val requiredLevel: Int = 1,
    val xpMult: Float = 1f,
    val coinMult: Float = 1f,
    val emoji: String = "\uD83D\uDCD6",
    val drawableName: String = ""
)

val shopTomes = listOf(
    // ---- Wisdom line: bonus XP ----
    Tome("tome_xp_1", "Apprentice's Tome", "+20% XP on your next victory",
        price = 30, category = "Wisdom \u00B7 bonus XP", requiredLevel = 1, xpMult = 1.20f, emoji = "\uD83D\uDCD8", drawableName = "tome_14"),
    Tome("tome_xp_2", "Tome of Wisdom", "+35% XP on your next victory",
        price = 55, category = "Wisdom \u00B7 bonus XP", requiredLevel = 2, xpMult = 1.35f, emoji = "\uD83D\uDCD8", drawableName = "tome_17"),
    Tome("tome_xp_3", "Scholar's Tome", "+50% XP on your next victory",
        price = 90, category = "Wisdom \u00B7 bonus XP", requiredLevel = 4, xpMult = 1.50f, emoji = "\uD83D\uDCD8", drawableName = "tome_05"),
    Tome("tome_xp_4", "Sage's Tome", "+75% XP on your next victory",
        price = 150, category = "Wisdom \u00B7 bonus XP", requiredLevel = 7, xpMult = 1.75f, emoji = "\uD83D\uDCD8", drawableName = "tome_44"),
    Tome("tome_xp_5", "Tome of Enlightenment", "+100% XP on your next victory",
        price = 220, category = "Wisdom \u00B7 bonus XP", requiredLevel = 10, xpMult = 2.00f, emoji = "\uD83D\uDCD8", drawableName = "tome_43"),

    // ---- Fortune line: bonus gold ----
    Tome("tome_gold_1", "Coin Pouch Codex", "+20% gold on your next victory",
        price = 30, category = "Fortune \u00B7 bonus gold", requiredLevel = 1, coinMult = 1.20f, emoji = "\uD83D\uDCD2", drawableName = "tome_22"),
    Tome("tome_gold_2", "Tome of Fortune", "+35% gold on your next victory",
        price = 55, category = "Fortune \u00B7 bonus gold", requiredLevel = 2, coinMult = 1.35f, emoji = "\uD83D\uDCD2", drawableName = "tome_16"),
    Tome("tome_gold_3", "Merchant's Tome", "+50% gold on your next victory",
        price = 90, category = "Fortune \u00B7 bonus gold", requiredLevel = 4, coinMult = 1.50f, emoji = "\uD83D\uDCD2", drawableName = "tome_18"),
    Tome("tome_gold_4", "Tome of Riches", "+75% gold on your next victory",
        price = 150, category = "Fortune \u00B7 bonus gold", requiredLevel = 7, coinMult = 1.75f, emoji = "\uD83D\uDCD2", drawableName = "tome_12"),
    Tome("tome_gold_5", "Tome of Avarice", "+100% gold on your next victory",
        price = 220, category = "Fortune \u00B7 bonus gold", requiredLevel = 10, coinMult = 2.00f, emoji = "\uD83D\uDCD2", drawableName = "tome_10"),

    // ---- Insight line: bonus XP & gold ----
    Tome("tome_both_1", "Tome of Insight", "+30% XP & gold on your next victory",
        price = 160, category = "Insight \u00B7 XP & gold", requiredLevel = 5, xpMult = 1.30f, coinMult = 1.30f, emoji = "\uD83D\uDD2E", drawableName = "tome_02"),
    Tome("tome_both_2", "Arcane Codex", "+50% XP & gold on your next victory",
        price = 260, category = "Insight \u00B7 XP & gold", requiredLevel = 8, xpMult = 1.50f, coinMult = 1.50f, emoji = "\uD83D\uDD2E", drawableName = "tome_07"),
    Tome("tome_both_3", "Mystic Grimoire", "+75% XP & gold on your next victory",
        price = 420, category = "Insight \u00B7 XP & gold", requiredLevel = 12, xpMult = 1.75f, coinMult = 1.75f, emoji = "\uD83D\uDD2E", drawableName = "tome_20"),

    // ---- Legendary ----
    Tome("tome_legend_xp", "Forbidden Tome", "+150% XP on your next victory",
        price = 600, category = "Legendary", requiredLevel = 15, xpMult = 2.50f, emoji = "\uD83D\uDCD5", drawableName = "tome_31"),
    Tome("tome_legend_gold", "Haunted Ledger", "+150% gold on your next victory",
        price = 600, category = "Legendary", requiredLevel = 15, coinMult = 2.50f, emoji = "\uD83D\uDCD5", drawableName = "tome_06"),
)