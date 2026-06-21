package com.example.taskmanagement.presentation.achievements

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagement.data.local.AppDatabase
import com.example.taskmanagement.data.local.models.Achievement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AchievementsViewModel : ViewModel() {

    data class UiState(
        val items: List<Achievement> = emptyList(),
        val unlockedCount: Int = 0,
        val totalCount: Int = 0,
        val currentValues: Map<String, Int> = emptyMap(),
        val newlyUnlocked: List<Achievement> = emptyList()
    )

    private val _ui = MutableStateFlow(UiState())
    val ui = _ui.asStateFlow()

    private var collecting = false

    fun load(context: Context) {
        val appContext = context.applicationContext

        viewModelScope.launch {
            val newly = AchievementTracker.checkAndUnlock(appContext)
            val stats = AchievementTracker.currentStats(appContext)
            _ui.update {
                it.copy(
                    currentValues = stats,
                    newlyUnlocked = if (newly.isNotEmpty()) newly else it.newlyUnlocked
                )
            }
        }

        if (collecting) return
        collecting = true
        viewModelScope.launch {
            AppDatabase.getDatabase(appContext).achievementDao().getAll().collect { list ->
                _ui.update {
                    it.copy(
                        items = list,
                        totalCount = list.size,
                        unlockedCount = list.count { a -> a.isUnlocked }
                    )
                }
            }
        }
    }

    fun consumeNewlyUnlocked() {
        _ui.update { it.copy(newlyUnlocked = emptyList()) }
    }
}