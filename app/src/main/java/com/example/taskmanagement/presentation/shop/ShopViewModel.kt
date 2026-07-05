package com.example.taskmanagement.presentation.shop

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
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

    private val _tomeCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val tomeCounts = _tomeCounts.asStateFlow()

    private var collecting = false

    fun load(context: Context) {
        if (collecting) return
        collecting = true
        val database = AppDatabase.getDatabase(context.applicationContext)
        val dao = database.gameProfileDao()
        viewModelScope.launch {
            dao.createProfile()
            dao.getProfile().collect { _profile.value = it }
        }
        viewModelScope.launch {
            database.profileTomeDao().observeAll(PROFILE_ID).collect { tomes ->
                _tomeCounts.value = tomes.associate { it.tomeId to it.count }
            }
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
        val database = AppDatabase.getDatabase(context.applicationContext)
        viewModelScope.launch {
            database.withTransaction {
                val dao = database.gameProfileDao()
                val freshProfile = dao.getProfile().first() ?: return@withTransaction
                if (freshProfile.level < tome.requiredLevel) return@withTransaction
                if (freshProfile.coins < tome.price) return@withTransaction

                dao.updateProfile(
                    freshProfile.copy(
                        coins = (freshProfile.coins - tome.price).coerceAtLeast(0)
                    )
                )
                database.profileTomeDao().increment(PROFILE_ID, tome.id)
            }
        }
    }

    companion object {
        private const val PROFILE_ID = 1
    }
}
