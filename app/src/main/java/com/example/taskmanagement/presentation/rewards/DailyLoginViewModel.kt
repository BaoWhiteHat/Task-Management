package com.example.taskmanagement.presentation.rewards

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.taskmanagement.data.local.AppDatabase
import com.example.taskmanagement.data.local.models.GameProfile
import com.example.taskmanagement.data.local.models.ProfileBackground
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
        if (s.claimedToday) {
            _state.value = s.copy(visible = false)
            return
        }
        val reward = loginRewards.firstOrNull { it.day == s.cycleDay } ?: return
        val db = AppDatabase.getDatabase(context.applicationContext)
        val dao = db.gameProfileDao()
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            var resultingProfile: GameProfile? = null

            db.withTransaction {
                val freshProfile = dao.getProfile().first() ?: return@withTransaction
                if (freshProfile.lastLoginDate == today) {
                    resultingProfile = freshProfile
                    return@withTransaction
                }

                val updated = freshProfile.copy(
                    coins = freshProfile.coins + reward.coins,
                    lastLoginDate = today,
                    loginStreak = s.streak
                )
                dao.updateProfile(updated)
                if (reward.backgroundId != null) {
                    db.profileBackgroundDao().unlock(
                        ProfileBackground(PROFILE_ID, reward.backgroundId)
                    )
                }
                if (reward.tomeId != null) {
                    db.profileTomeDao().increment(PROFILE_ID, reward.tomeId)
                }
                if (reward.lootId != null) {
                    db.lootInventoryDao().addItem(reward.lootId, 1)
                }
                resultingProfile = updated
            }

            profile = resultingProfile ?: return@launch
            _state.value = s.copy(claimedToday = true)
        }
    }

    fun dismiss() {
        _state.value = _state.value.copy(visible = false)
    }

    companion object {
        private const val PROFILE_ID = 1
    }
}
