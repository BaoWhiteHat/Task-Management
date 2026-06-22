package com.example.taskmanagement.presentation.rewards

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagement.data.local.AppDatabase
import com.example.taskmanagement.data.local.models.GameProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DailyLoginState(
    val visible: Boolean = false,
    val cycleDay: Int = 1,
    val streak: Int = 0,
    val claimedToday: Boolean = false
)

class DailyLoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(DailyLoginState())
    val state = _state.asStateFlow()

    private var profile: GameProfile? = null
    private var checked = false

    fun checkLogin(context: Context) {
        if (checked) return
        checked = true
        val dao = AppDatabase.getDatabase(context.applicationContext).gameProfileDao()
        viewModelScope.launch {
            dao.createProfile()
            val p = dao.getProfile().first() ?: return@launch
            profile = p

            val today = LocalDate.now().toString()
            val yesterday = LocalDate.now().minusDays(1).toString()

            if (p.lastLoginDate == today) {
                val cd = ((p.loginStreak - 1).coerceAtLeast(0) % 7) + 1
                _state.value = DailyLoginState(
                    visible = false,
                    cycleDay = cd,
                    streak = p.loginStreak,
                    claimedToday = true
                )
                return@launch
            }

            val continued = p.lastLoginDate == yesterday
            val newStreak = if (continued) p.loginStreak + 1 else 1
            val cycleDay = ((newStreak - 1) % 7) + 1

            _state.value = DailyLoginState(
                visible = true,
                cycleDay = cycleDay,
                streak = newStreak,
                claimedToday = false
            )
        }
    }

    fun claim(context: Context) {
        val s = _state.value
        if (s.claimedToday) {        // safety: nothing to claim
            _state.value = s.copy(visible = false)
            return
        }
        val reward = loginRewards.firstOrNull { it.day == s.cycleDay } ?: return
        val dao = AppDatabase.getDatabase(context.applicationContext).gameProfileDao()
        viewModelScope.launch {
            val p = dao.getProfile().first() ?: return@launch
            val today = LocalDate.now().toString()
            if (p.lastLoginDate == today) {
                profile = p
                _state.value = s.copy(claimedToday = true)
                return@launch
            }
            val newInventory =
                if (reward.tomeId != null) incInventory(p.tomeInventory, reward.tomeId)
                else p.tomeInventory
            val newBackgrounds =
                if (reward.backgroundId != null) appendId(p.unlockedBackgrounds, reward.backgroundId)
                else p.unlockedBackgrounds
            val updated = p.copy(
                coins = p.coins + reward.coins,
                tomeInventory = newInventory,
                unlockedBackgrounds = newBackgrounds,
                lastLoginDate = today,
                loginStreak = s.streak
            )
            dao.updateProfile(updated)
            profile = updated
            _state.value = s.copy(claimedToday = true)   // stay visible to show the confirmation
        }
    }

    fun dismiss() {
        _state.value = _state.value.copy(visible = false)
    }

    private fun appendId(csv: String, id: String): String = when {
        csv.isBlank() -> id
        csv.split(",").contains(id) -> csv
        else -> "$csv,$id"
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