package com.example.taskmanagement.presentation.shop

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.taskmanagement.data.local.AppDatabase
import com.example.taskmanagement.data.local.models.GameProfile
import com.example.taskmanagement.data.local.models.ProfileBackground
import com.example.taskmanagement.data.local.models.ProfileSound
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

    private val _unlockedSoundIds = MutableStateFlow<Set<String>>(emptySet())
    val unlockedSoundIds = _unlockedSoundIds.asStateFlow()

    private val _unlockedBackgroundIds = MutableStateFlow<Set<String>>(emptySet())
    val unlockedBackgroundIds = _unlockedBackgroundIds.asStateFlow()

    private val _guardianItemCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val guardianItemCounts = _guardianItemCounts.asStateFlow()

    private var collecting = false

    fun load(context: Context) {
        if (collecting) return
        collecting = true
        val database = AppDatabase.getDatabase(context.applicationContext)
        val dao = database.gameProfileDao()
        viewModelScope.launch {
            database.withTransaction {
                dao.createProfile()
                database.profileSoundDao().insertDefaults(
                    listOf(ProfileSound(PROFILE_ID, DEFAULT_SOUND_ID))
                )
                database.profileBackgroundDao().insertDefaults(
                    shopBackgrounds
                        .filter { it.price == 0 }
                        .map { ProfileBackground(PROFILE_ID, it.id) }
                )
            }
            dao.getProfile().collect { _profile.value = it }
        }
        viewModelScope.launch {
            database.profileSoundDao().observeUnlockedSounds(PROFILE_ID).collect {
                _unlockedSoundIds.value = it.toSet()
            }
        }
        viewModelScope.launch {
            database.profileBackgroundDao().observeUnlockedBackgrounds(PROFILE_ID).collect {
                _unlockedBackgroundIds.value = it.toSet()
            }
        }
        viewModelScope.launch {
            database.profileTomeDao().observeAll(PROFILE_ID).collect { tomes ->
                _tomeCounts.value = tomes.associate { it.tomeId to it.count }
            }
        }
        viewModelScope.launch {
            database.lootInventoryDao().getOwned().collect { items ->
                _guardianItemCounts.value = items
                    .filter { it.itemId in GUARDIAN_ITEM_IDS }
                    .associate { it.itemId to it.count }
            }
        }
    }

    fun buyBackground(context: Context, bg: ShopBackground) {
        val database = AppDatabase.getDatabase(context.applicationContext)
        viewModelScope.launch {
            database.withTransaction {
                val profileDao = database.gameProfileDao()
                val backgroundDao = database.profileBackgroundDao()
                val freshProfile = profileDao.getProfile().first() ?: return@withTransaction
                if (backgroundDao.isUnlocked(PROFILE_ID, bg.id)) return@withTransaction
                if (freshProfile.level < bg.requiredLevel) return@withTransaction
                if (freshProfile.coins < bg.price) return@withTransaction

                backgroundDao.unlock(ProfileBackground(PROFILE_ID, bg.id))
                profileDao.updateProfile(
                    freshProfile.copy(
                        coins = (freshProfile.coins - bg.price).coerceAtLeast(0),
                        selectedBackgroundId = bg.id
                    )
                )
            }
        }
    }

    fun equipBackground(context: Context, id: String) {
        val database = AppDatabase.getDatabase(context.applicationContext)
        viewModelScope.launch {
            database.withTransaction {
                if (id.isNotBlank() &&
                    !database.profileBackgroundDao().isUnlocked(PROFILE_ID, id)
                ) return@withTransaction

                val dao = database.gameProfileDao()
                val freshProfile = dao.getProfile().first() ?: return@withTransaction
                dao.updateProfile(freshProfile.copy(selectedBackgroundId = id))
            }
        }
    }

    fun buySound(context: Context, sound: AmbientSound) {
        val database = AppDatabase.getDatabase(context.applicationContext)
        viewModelScope.launch {
            database.withTransaction {
                val profileDao = database.gameProfileDao()
                val soundDao = database.profileSoundDao()
                val freshProfile = profileDao.getProfile().first() ?: return@withTransaction
                if (soundDao.isUnlocked(PROFILE_ID, sound.id)) return@withTransaction
                if (freshProfile.coins < sound.price) return@withTransaction

                soundDao.unlock(ProfileSound(PROFILE_ID, sound.id))
                profileDao.updateProfile(
                    freshProfile.copy(
                        coins = (freshProfile.coins - sound.price).coerceAtLeast(0)
                    )
                )
            }
        }
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

    fun buyGuardianItem(context: Context, item: GuardianItem) {
        val database = AppDatabase.getDatabase(context.applicationContext)
        viewModelScope.launch {
            database.withTransaction {
                val profileDao = database.gameProfileDao()
                val freshProfile = profileDao.getProfile().first() ?: return@withTransaction
                if (freshProfile.coins < item.price) return@withTransaction

                profileDao.updateProfile(
                    freshProfile.copy(coins = freshProfile.coins - item.price)
                )
                database.lootInventoryDao().addItem(item.id, 1)
            }
        }
    }

    fun activateLootMagnet(context: Context) {
        val database = AppDatabase.getDatabase(context.applicationContext)
        viewModelScope.launch {
            var activated = false
            database.withTransaction {
                val lootDao = database.lootInventoryDao()
                if ((lootDao.get(GuardianItemIds.LOOT_MAGNET_ACTIVE)?.count ?: 0) > 0) {
                    return@withTransaction
                }
                if (!lootDao.consumeOne(GuardianItemIds.LOOT_MAGNET)) return@withTransaction
                lootDao.addItem(GuardianItemIds.LOOT_MAGNET_ACTIVE, 1)
                activated = true
            }
            if (activated) {
                Toast.makeText(
                    context.applicationContext,
                    "Loot Magnet activated. Your next victory will attract extra treasure.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {
        private const val PROFILE_ID = 1
        private const val DEFAULT_SOUND_ID = "rain"
        private val GUARDIAN_ITEM_IDS = guardianItems.mapTo(mutableSetOf()) { it.id } +
            GuardianItemIds.LOOT_MAGNET_ACTIVE
    }
}
