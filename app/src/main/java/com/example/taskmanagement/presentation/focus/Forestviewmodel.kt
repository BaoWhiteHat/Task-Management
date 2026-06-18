package com.example.taskmanagement.presentation.focus

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagement.data.local.AppDatabase
import com.example.taskmanagement.data.local.models.FocusSession
import com.example.taskmanagement.data.local.models.GameProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForestViewModel : ViewModel() {

    private val _sessions = MutableStateFlow<List<FocusSession>>(emptyList())
    val sessions = _sessions.asStateFlow()

    private val _profile = MutableStateFlow<GameProfile?>(null)
    val profile = _profile.asStateFlow()

    private var loaded = false

    fun load(context: Context) {
        if (loaded) return
        loaded = true

        val db = AppDatabase.getDatabase(context.applicationContext)

        viewModelScope.launch {
            db.focusSessionDao().getAllFocusSessions().collect { list ->
                _sessions.value = list
            }
        }
        viewModelScope.launch {
            db.gameProfileDao().getProfile().collect { p ->
                _profile.value = p
            }
        }
    }
}