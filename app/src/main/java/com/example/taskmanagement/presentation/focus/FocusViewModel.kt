package com.example.taskmanagement.presentation.focus

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagement.data.local.AppDatabase
import com.example.taskmanagement.data.local.models.FocusSession
import com.example.taskmanagement.presentation.focus.utils.SoundPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class FocusViewModel : ViewModel() {

    companion object {
        private const val FAST_TEST_MODE = true

        private val TIMER_TICK_MS: Long
            get() = if (FAST_TEST_MODE) 20L else 1000L
    }

    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var completedSessionsJob: Job? = null

    fun loadTodayCompletedSessions(context: Context) {
        if (completedSessionsJob != null) return

        val appContext = context.applicationContext

        completedSessionsJob = viewModelScope.launch {
            AppDatabase
                .getDatabase(appContext)
                .focusSessionDao()
                .getCompletedCountForDate(LocalDate.now())
                .collect { count ->
                    _uiState.update {
                        it.copy(completedStudySessions = count)
                    }
                }
        }
    }

    fun selectPreset(index: Int) {
        if (_uiState.value.isRunning || index !in focusPresets.indices) return

        val preset = focusPresets[index]

        _uiState.update {
            it.copy(
                selectedPresetIndex = index,
                phase = FocusPhase.STUDY,
                timeLeft = preset.studySeconds,
                showBreakActivityPopup = false,
                breakActivitySuggestion = null
            )
        }
    }

    fun start(
        context: Context,
        taskTitle: String = ""
    ) {
        if (_uiState.value.isRunning) return

        _uiState.update { it.copy(isRunning = true) }

        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeft > 0) {
                delay(TIMER_TICK_MS)

                _uiState.update {
                    it.copy(
                        timeLeft = (it.timeLeft - 1).coerceAtLeast(0)
                    )
                }
            }

            SoundPlayer.play(context)

            moveToNextPhase(
                context = context,
                taskTitle = taskTitle,
                completedNaturally = true
            )
        }
    }

    fun pause() {
        timerJob?.cancel()
        timerJob = null

        _uiState.update {
            it.copy(isRunning = false)
        }
    }

    fun reset() {
        timerJob?.cancel()
        timerJob = null

        val preset = _uiState.value.selectedPreset

        _uiState.update {
            it.copy(
                phase = FocusPhase.STUDY,
                timeLeft = preset.studySeconds,
                isRunning = false,
                showBreakActivityPopup = false,
                breakActivitySuggestion = null
            )
        }
    }

    fun skipPhase() {
        timerJob?.cancel()
        timerJob = null

        moveToNextPhase(
            context = null,
            taskTitle = "",
            completedNaturally = false
        )
    }

    fun dismissBreakActivityPopup() {
        _uiState.update {
            it.copy(showBreakActivityPopup = false)
        }
    }

    fun randomizeBreakActivity() {
        _uiState.update {
            it.copy(
                breakActivitySuggestion = getRandomBreakSuggestion(
                    currentSuggestion = it.breakActivitySuggestion
                )
            )
        }
    }

    private fun moveToNextPhase(
        context: Context?,
        taskTitle: String,
        completedNaturally: Boolean
    ) {
        val currentState = _uiState.value

        val nextPhase = if (currentState.phase == FocusPhase.STUDY) {
            FocusPhase.BREAK
        } else {
            FocusPhase.STUDY
        }

        val nextTime = if (nextPhase == FocusPhase.BREAK) {
            currentState.selectedPreset.breakSeconds
        } else {
            currentState.selectedPreset.studySeconds
        }

        val completedStudyNaturally =
            currentState.phase == FocusPhase.STUDY && completedNaturally

        if (completedStudyNaturally && context != null) {
            saveCompletedFocusSession(
                context = context,
                taskTitle = taskTitle,
                studyMinutes = currentState.selectedPreset.studyMinutes,
                breakMinutes = currentState.selectedPreset.breakMinutes
            )
        }

        _uiState.update {
            it.copy(
                phase = nextPhase,
                timeLeft = nextTime,
                isRunning = false,

                completedStudySessions = if (completedStudyNaturally) {
                    it.completedStudySessions + 1
                } else {
                    it.completedStudySessions
                },

                showBreakActivityPopup = completedStudyNaturally,
                breakActivitySuggestion = if (completedStudyNaturally) {
                    getRandomBreakSuggestion(it.breakActivitySuggestion)
                } else {
                    it.breakActivitySuggestion
                }
            )
        }
    }

    private fun saveCompletedFocusSession(
        context: Context,
        taskTitle: String,
        studyMinutes: Int,
        breakMinutes: Int
    ) {
        val appContext = context.applicationContext

        viewModelScope.launch {
            AppDatabase
                .getDatabase(appContext)
                .focusSessionDao()
                .insertFocusSession(
                    FocusSession(
                        taskTitle = taskTitle,
                        studyMinutes = studyMinutes,
                        breakMinutes = breakMinutes,
                        completedDate = LocalDate.now()
                    )
                )
        }
    }

    private fun getRandomBreakSuggestion(
        currentSuggestion: BreakActivitySuggestion? = null
    ): BreakActivitySuggestion {
        val suggestions = if (
            currentSuggestion == null ||
            breakActivitySuggestions.size <= 1
        ) {
            breakActivitySuggestions
        } else {
            breakActivitySuggestions.filter { it != currentSuggestion }
        }

        return suggestions.random()
    }

    override fun onCleared() {
        timerJob?.cancel()
        completedSessionsJob?.cancel()
        super.onCleared()
    }
}