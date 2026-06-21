package com.example.taskmanagement.presentation.shop

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagement.data.local.AppDatabase
import com.example.taskmanagement.data.local.models.GameProfile
import com.example.taskmanagement.presentation.focus.AmbientSound
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    // Buy a background: deduct coins, add to owned list, auto-equip. All in ONE atomic update.
    fun buyBackground(context: Context, bg: ShopBackground) {
        val dao = AppDatabase.getDatabase(context.applicationContext).gameProfileDao()
        viewModelScope.launch {
            val p = _profile.value ?: return@launch
            if (bg.price == 0 || p.hasBackground(bg.id)) return@launch   // already free / owned
            if (p.level < bg.requiredLevel) return@launch                // level too low
            if (p.coins < bg.price) return@launch                        // not enough coins
            dao.updateProfile(
                p.copy(
                    coins = (p.coins - bg.price).coerceAtLeast(0),
                    unlockedBackgrounds = appendId(p.unlockedBackgrounds, bg.id),
                    selectedBackgroundId = bg.id
                )
            )
        }
    }

    // Equip a background you already own. id = "" means default day/night scene.
    fun equipBackground(context: Context, id: String) {
        val dao = AppDatabase.getDatabase(context.applicationContext).gameProfileDao()
        viewModelScope.launch {
            val p = _profile.value ?: return@launch
            dao.updateProfile(p.copy(selectedBackgroundId = id))
        }
    }

    // Buy a sound: deduct coins + add to owned list, atomically.
    fun buySound(context: Context, sound: AmbientSound) {
        val dao = AppDatabase.getDatabase(context.applicationContext).gameProfileDao()
        viewModelScope.launch {
            val p = _profile.value ?: return@launch
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
}