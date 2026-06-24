package com.example.taskmanagement.presentation.shop

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagement.data.local.AppDatabase
import com.example.taskmanagement.data.local.models.GameProfile
import com.example.taskmanagement.presentation.focus.AmbientSound
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ShopViewModel : ViewModel() {

    private val _profile = MutableStateFlow<GameProfile?>(null)
    val profile = _profile.asStateFlow()

    private var collecting = false

    fun load(context: Context) {
        if (collecting) return
        collecting = true
        val dao = AppDatabase.getDatabase(context.applicationContext).gameProfileDao()
        viewModelScope.launch {
            dao.createProfile()
            dao.getProfile().collect { _profile.value = it }
        }
    }

    fun buyBackground(context: Context, bg: ShopBackground) {
        val dao = AppDatabase.getDatabase(context.applicationContext).gameProfileDao()
        viewModelScope.launch {
            val p = dao.getProfile().first() ?: return@launch
            if (bg.price == 0 || p.hasBackground(bg.id)) return@launch
            if (p.level < bg.requiredLevel) return@launch
            if (p.coins < bg.price) return@launch
            dao.updateProfile(
                p.copy(
                    coins = (p.coins - bg.price).coerceAtLeast(0),
                    unlockedBackgrounds = appendId(p.unlockedBackgrounds, bg.id),
                    selectedBackgroundId = bg.id
                )
            )
        }
    }

    fun equipBackground(context: Context, id: String) {
        val dao = AppDatabase.getDatabase(context.applicationContext).gameProfileDao()
        viewModelScope.launch {
            val p = dao.getProfile().first() ?: return@launch
            dao.updateProfile(p.copy(selectedBackgroundId = id))
        }
    }

    fun buySound(context: Context, sound: AmbientSound) {
        val dao = AppDatabase.getDatabase(context.applicationContext).gameProfileDao()
        viewModelScope.launch {
            val p = dao.getProfile().first() ?: return@launch
            if (sound.price == 0 || p.hasSound(sound.id)) return@launch
            if (p.coins < sound.price) return@launch
            dao.updateProfile(
                p.copy(
                    coins = (p.coins - sound.price).coerceAtLeast(0),
                    unlockedSounds = appendId(p.unlockedSounds, sound.id)
                )
            )
        }
    }

    private fun appendId(csv: String, id: String): String = when {
        csv.isBlank() -> id
        csv.split(",").contains(id) -> csv
        else -> "$csv,$id"
    }

    fun buyTome(context: Context, tome: Tome) {
        val dao = AppDatabase.getDatabase(context.applicationContext).gameProfileDao()
        viewModelScope.launch {
            val p = dao.getProfile().first() ?: return@launch
            if (p.level < tome.requiredLevel) return@launch
            if (p.coins < tome.price) return@launch
            dao.updateProfile(
                p.copy(
                    coins = (p.coins - tome.price).coerceAtLeast(0),
                    tomeInventory = incInventory(p.tomeInventory, tome.id)
                )
            )
        }
    }

    private fun incInventory(csv: String, id: String): String {
        val map = LinkedHashMap<String, Int>()
        if (csv.isNotBlank()) {
            for (part in csv.split(",")) {
                val kv = part.split(":")
                if (kv.size == 2) map[kv[0]] = kv[1].toIntOrNull() ?: 0
            }
        }
        map[id] = (map[id] ?: 0) + 1
        return map.entries.joinToString(",") { "${it.key}:${it.value}" }
    }

}