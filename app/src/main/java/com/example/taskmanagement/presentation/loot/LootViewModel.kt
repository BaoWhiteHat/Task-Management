package com.example.taskmanagement.presentation.loot

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagement.data.local.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LootViewModel : ViewModel() {

    private val _owned = MutableStateFlow<Map<String, Int>>(emptyMap())
    val owned = _owned.asStateFlow()

    private val _coins = MutableStateFlow(0)
    val coins = _coins.asStateFlow()

    private var started = false

    fun start(context: Context) {
        if (started) return
        started = true
        val db = AppDatabase.getDatabase(context.applicationContext)
        viewModelScope.launch {
            db.lootInventoryDao().getOwned().collect { list ->
                _owned.value = list
                    .filter { lootItemById(it.itemId) != null }
                    .associate { it.itemId to it.count }
            }
        }
        viewModelScope.launch {
            db.gameProfileDao().getProfile().collect { p ->
                _coins.value = p?.coins ?: 0
            }
        }
    }

    fun sellOne(context: Context, itemId: String) {
        val item = lootItemById(itemId) ?: return
        val db = AppDatabase.getDatabase(context.applicationContext)
        viewModelScope.launch {
            val current = db.lootInventoryDao().get(itemId)?.count ?: 0
            if (current <= 0) return@launch
            db.lootInventoryDao().removeItem(itemId, 1)
            db.gameProfileDao().addCoins(item.rarity.sellPrice)
        }
    }

    fun sellAllDuplicates(context: Context) {
        val db = AppDatabase.getDatabase(context.applicationContext)
        viewModelScope.launch {
            val list = db.lootInventoryDao().getOwned().first()
            var total = 0
            for (row in list) {
                val item = lootItemById(row.itemId) ?: continue
                val extra = row.count - 1
                if (extra > 0) {
                    val price = item.rarity.sellPrice
                    db.lootInventoryDao().removeItem(row.itemId, extra)
                    total += extra * price
                }
            }
            if (total > 0) db.gameProfileDao().addCoins(total)
        }
    }
}
