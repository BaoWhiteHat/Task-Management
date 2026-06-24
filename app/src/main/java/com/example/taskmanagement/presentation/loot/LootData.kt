package com.example.taskmanagement.presentation.loot

import kotlin.random.Random

enum class LootRarity(val label: String, val sellPrice: Int, val weight: Int) {
    COMMON("Common", 6, 60),
    UNCOMMON("Uncommon", 15, 28),
    RARE("Rare", 40, 10),
    EPIC("Epic", 100, 2)
}

data class LootItem(
    val id: String,
    val name: String,
    val rarity: LootRarity,
    val drawableName: String
)

val lootItems: List<LootItem> = listOf(
    LootItem("item_01", "Ember Coral", LootRarity.UNCOMMON, "item_01"),
    LootItem("item_02", "Charred Pelt", LootRarity.COMMON, "item_02"),
    LootItem("item_03", "Golden Wheat", LootRarity.COMMON, "item_03"),
    LootItem("item_04", "Sandstar", LootRarity.COMMON, "item_04"),
    LootItem("item_05", "Azure Scale", LootRarity.RARE, "item_05"),
    LootItem("item_06", "Pearl Shell", LootRarity.UNCOMMON, "item_06"),
    LootItem("item_07", "Ivory Fang", LootRarity.UNCOMMON, "item_07"),
    LootItem("item_08", "Bone Talons", LootRarity.UNCOMMON, "item_08"),
    LootItem("item_09", "Pale Tusk", LootRarity.COMMON, "item_09"),
    LootItem("item_10", "Bleached Antler", LootRarity.COMMON, "item_10"),
    LootItem("item_11", "Oak Stake", LootRarity.COMMON, "item_11"),
    LootItem("item_12", "Spice Cloves", LootRarity.COMMON, "item_12"),
    LootItem("item_13", "Mossy Scale", LootRarity.COMMON, "item_13"),
    LootItem("item_14", "Teal Scales", LootRarity.RARE, "item_14"),
    LootItem("item_15", "Toad Hide", LootRarity.COMMON, "item_15"),
    LootItem("item_16", "Azure Plume", LootRarity.RARE, "item_16"),
    LootItem("item_17", "Amethyst Shard", LootRarity.EPIC, "item_17"),
    LootItem("item_18", "Wild Herbs", LootRarity.COMMON, "item_18"),
    LootItem("item_19", "Spiral Shell", LootRarity.COMMON, "item_19"),
    LootItem("item_20", "Bone Fragments", LootRarity.COMMON, "item_20"),
    LootItem("item_21", "Curved Horn", LootRarity.COMMON, "item_21"),
    LootItem("item_22", "Pale Claws", LootRarity.UNCOMMON, "item_22"),
    LootItem("item_23", "Jade Toad", LootRarity.RARE, "item_23"),
    LootItem("item_24", "Forked Bone", LootRarity.COMMON, "item_24"),
    LootItem("item_25", "Dark Seed", LootRarity.COMMON, "item_25"),
    LootItem("item_26", "Acorn", LootRarity.COMMON, "item_26"),
    LootItem("item_27", "Fox Tail", LootRarity.UNCOMMON, "item_27"),
    LootItem("item_28", "Autumn Star", LootRarity.COMMON, "item_28"),
    LootItem("item_29", "Speckled Egg", LootRarity.UNCOMMON, "item_29"),
    LootItem("item_30", "Crimson Coral", LootRarity.UNCOMMON, "item_30"),
    LootItem("item_31", "Hooked Claw", LootRarity.UNCOMMON, "item_31"),
    LootItem("item_32", "White Plume", LootRarity.COMMON, "item_32"),
    LootItem("item_33", "Curved Tusk", LootRarity.UNCOMMON, "item_33"),
    LootItem("item_34", "Fanged Jaw", LootRarity.RARE, "item_34"),
    LootItem("item_35", "Quartz Point", LootRarity.UNCOMMON, "item_35"),
    LootItem("item_36", "Citrine Cluster", LootRarity.RARE, "item_36"),
    LootItem("item_37", "Pinecone", LootRarity.COMMON, "item_37"),
    LootItem("item_38", "Verdant Leaves", LootRarity.COMMON, "item_38"),
    LootItem("item_39", "Honeycomb", LootRarity.UNCOMMON, "item_39"),
    LootItem("item_40", "Crimson Plume", LootRarity.EPIC, "item_40"),
    LootItem("item_41", "Phoenix Plume", LootRarity.EPIC, "item_41"),
    LootItem("item_42", "Bloodthorn", LootRarity.UNCOMMON, "item_42"),
    LootItem("item_43", "Beast Horn", LootRarity.COMMON, "item_43"),
    LootItem("item_44", "Conch Shell", LootRarity.COMMON, "item_44"),
    LootItem("item_45", "Bone Splinters", LootRarity.COMMON, "item_45"),
    LootItem("item_46", "Frost Urchin", LootRarity.UNCOMMON, "item_46"),
    LootItem("item_47", "Jade Pod", LootRarity.COMMON, "item_47"),
    LootItem("item_48", "Cabbage Leaf", LootRarity.COMMON, "item_48")
)

fun lootItemById(id: String): LootItem? = lootItems.firstOrNull { it.id == id }

fun rollLootItem(): LootItem {
    val tiers = LootRarity.values()
    val totalWeight = tiers.sumOf { it.weight }
    var r = Random.nextInt(totalWeight)
    var chosen = LootRarity.COMMON
    for (t in tiers) {
        r -= t.weight
        if (r < 0) { chosen = t; break }
    }
    val pool = lootItems.filter { it.rarity == chosen }
    return if (pool.isEmpty()) lootItems.random() else pool[Random.nextInt(pool.size)]
}

fun rollLootItemOfRarity(rarity: LootRarity): LootItem {
    val pool = lootItems.filter { it.rarity == rarity }
    return if (pool.isEmpty()) lootItems.random() else pool[Random.nextInt(pool.size)]
}