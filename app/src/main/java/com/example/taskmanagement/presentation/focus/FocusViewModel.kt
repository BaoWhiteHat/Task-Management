package com.example.taskmanagement.presentation.focus

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagement.data.local.AppDatabase
import com.example.taskmanagement.data.local.models.FocusSession
import com.example.taskmanagement.data.local.models.GameProfile
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
    private var gameProfileJob: Job? = null
    private var ambientPlayer: MediaPlayer? = null

    // Load data

    fun loadTodayCompletedSessions(context: Context) {
        if (completedSessionsJob != null) return

        val appContext = context.applicationContext
        val db = AppDatabase.getDatabase(appContext)

        completedSessionsJob = viewModelScope.launch {
            db.focusSessionDao()
                .getCompletedCountForDate(LocalDate.now())
                .collect { count ->
                    _uiState.update { it.copy(completedStudySessions = count) }
                }
        }
    }

    fun loadGameProfile(context: Context) {
        if (gameProfileJob != null) return

        val appContext = context.applicationContext
        val dao = AppDatabase.getDatabase(appContext).gameProfileDao()

        gameProfileJob = viewModelScope.launch {
            dao.createProfile()
            dao.getProfile().collect { profile ->
                profile?.let {
                    _uiState.update { state ->
                        state.copy(gameProfile = it)
                    }
                }
            }
        }
    }

    // Preset & Sound selection

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

    fun selectSound(soundId: String) {
        _uiState.update { it.copy(selectedSoundId = soundId) }
    }

    fun unlockSound(context: Context, sound: AmbientSound) {
        val profile = _uiState.value.gameProfile ?: return
        if (profile.coins < sound.price) return
        if (profile.hasSound(sound.id)) return

        val appContext = context.applicationContext
        val dao = AppDatabase.getDatabase(appContext).gameProfileDao()

        viewModelScope.launch {
            dao.spendCoins(sound.price)
            val newSounds = profile.unlockedSounds + ",${sound.id}"
            dao.updateProfile(profile.copy(unlockedSounds = newSounds))
        }

        _uiState.update { it.copy(selectedSoundId = sound.id) }
    }

    // Timer controls

    fun start(context: Context, taskTitle: String = "") {
        if (_uiState.value.isRunning) return

        _uiState.update { it.copy(isRunning = true) }

        startAmbientSound(context)

        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeft > 0) {
                delay(TIMER_TICK_MS)
                _uiState.update {
                    it.copy(timeLeft = (it.timeLeft - 1).coerceAtLeast(0))
                }
            }

            SoundPlayer.play(context)
            stopAmbientSound()

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
        stopAmbientSound()
        _uiState.update { it.copy(isRunning = false) }
    }

    fun reset(context: Context? = null) {
        timerJob?.cancel()
        timerJob = null
        stopAmbientSound()

        val state = _uiState.value
        val preset = state.selectedPreset

        // Penalty: quit before 50% = lose XP & coins
        if (context != null && state.phase == FocusPhase.STUDY) {
            val elapsed = preset.studySeconds - state.timeLeft
            val halfWay = preset.studySeconds / 2
            if (elapsed > 0 && elapsed < halfWay) {
                applyPenalty(context)
            }
        }

        _uiState.update {
            it.copy(
                phase = FocusPhase.STUDY,
                timeLeft = preset.studySeconds,
                isRunning = false,
                showBreakActivityPopup = false,
                breakActivitySuggestion = null,
                showPenaltyWarning = false
            )
        }
    }

    fun requestReset() {
        val state = _uiState.value
        if (state.phase == FocusPhase.STUDY && state.isRunning) {
            val elapsed = state.selectedPreset.studySeconds - state.timeLeft
            val halfWay = state.selectedPreset.studySeconds / 2
            if (elapsed > 0 && elapsed < halfWay) {
                _uiState.update { it.copy(showPenaltyWarning = true) }
                return
            }
        }
        reset()
    }

    fun dismissPenaltyWarning() {
        _uiState.update { it.copy(showPenaltyWarning = false) }
    }

    fun skipPhase() {
        timerJob?.cancel()
        timerJob = null
        stopAmbientSound()

        moveToNextPhase(
            context = null,
            taskTitle = "",
            completedNaturally = false
        )
    }

    // Break activity popup

    fun dismissBreakActivityPopup() {
        _uiState.update { it.copy(showBreakActivityPopup = false) }
    }

    fun randomizeBreakActivity() {
        _uiState.update {
            it.copy(
                breakActivitySuggestion = getRandomBreakSuggestion(it.breakActivitySuggestion)
            )
        }
    }

    // Phase transition

    private fun moveToNextPhase(
        context: Context?,
        taskTitle: String,
        completedNaturally: Boolean
    ) {
        val currentState = _uiState.value

        val nextPhase = if (currentState.phase == FocusPhase.STUDY)
            FocusPhase.BREAK else FocusPhase.STUDY

        val nextTime = if (nextPhase == FocusPhase.BREAK)
            currentState.selectedPreset.breakSeconds
        else
            currentState.selectedPreset.studySeconds

        val completedStudyNaturally =
            currentState.phase == FocusPhase.STUDY && completedNaturally

        if (completedStudyNaturally && context != null) {
            saveCompletedFocusSession(
                context = context,
                taskTitle = taskTitle,
                studyMinutes = currentState.selectedPreset.studyMinutes,
                breakMinutes = currentState.selectedPreset.breakMinutes
            )
            rewardXpAndCoins(context, currentState.selectedPreset)
        }

        _uiState.update {
            it.copy(
                phase = nextPhase,
                timeLeft = nextTime,
                isRunning = false,
                completedStudySessions = if (completedStudyNaturally)
                    it.completedStudySessions + 1 else it.completedStudySessions,
                showBreakActivityPopup = completedStudyNaturally,
                breakActivitySuggestion = if (completedStudyNaturally)
                    getRandomBreakSuggestion(it.breakActivitySuggestion)
                else it.breakActivitySuggestion
            )
        }
    }

    // XP & Coins

    private fun rewardXpAndCoins(context: Context, preset: FocusPreset) {
        val appContext = context.applicationContext
        val dao = AppDatabase.getDatabase(appContext).gameProfileDao()

        val xpReward = if (preset.studyMinutes >= 50) 60 else 30
        val coinReward = if (preset.studyMinutes >= 50) 20 else 10

        viewModelScope.launch {
            dao.addXp(xpReward)
            dao.addCoins(coinReward)

            // Check level up
            val profile = _uiState.value.gameProfile ?: return@launch
            val newXp = profile.xp + xpReward
            val needed = profile.xpForNextLevel
            if (newXp >= needed) {
                dao.levelUp(overflow = newXp - needed)
            }

            // Update streak
            val today = LocalDate.now().toString()
            if (profile.lastFocusDate != today) {
                val yesterday = LocalDate.now().minusDays(1).toString()
                val newStreak = if (profile.lastFocusDate == yesterday)
                    profile.streakDays + 1 else 1
                dao.updateProfile(
                    profile.copy(
                        lastFocusDate = today,
                        streakDays = newStreak,
                        totalSessions = profile.totalSessions + 1
                    )
                )
            } else {
                dao.updateProfile(
                    profile.copy(totalSessions = profile.totalSessions + 1)
                )
            }
        }
    }

    private fun applyPenalty(context: Context) {
        val appContext = context.applicationContext
        val dao = AppDatabase.getDatabase(appContext).gameProfileDao()

        viewModelScope.launch {
            val profile = _uiState.value.gameProfile ?: return@launch
            val newXp = (profile.xp - 15).coerceAtLeast(0)
            val newCoins = (profile.coins - 5).coerceAtLeast(0)
            dao.updateProfile(
                profile.copy(
                    xp = newXp,
                    coins = newCoins,
                    streakDays = 0
                )
            )
        }
    }

    // Ambient sound

    private fun startAmbientSound(context: Context) {
        stopAmbientSound()
        val soundId = _uiState.value.selectedSoundId ?: return
        val profile = _uiState.value.gameProfile ?: return
        if (!profile.hasSound(soundId)) return

        val resId = context.resources.getIdentifier(soundId, "raw", context.packageName)
        if (resId == 0) return

        ambientPlayer = MediaPlayer.create(context, resId)?.apply {
            isLooping = true
            setVolume(0.3f, 0.3f)
            start()
        }
    }

    private fun stopAmbientSound() {
        ambientPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        ambientPlayer = null
    }

    // Save session

    private fun saveCompletedFocusSession(
        context: Context,
        taskTitle: String,
        studyMinutes: Int,
        breakMinutes: Int
    ) {
        val appContext = context.applicationContext

        viewModelScope.launch {
            AppDatabase.getDatabase(appContext)
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

    // Helpers
    private fun getRandomBreakSuggestion(
        currentSuggestion: BreakActivitySuggestion? = null
    ): BreakActivitySuggestion {
        val suggestions = if (currentSuggestion == null || breakActivitySuggestions.size <= 1)
            breakActivitySuggestions
        else
            breakActivitySuggestions.filter { it != currentSuggestion }
        return suggestions.random()
    }

    override fun onCleared() {
        timerJob?.cancel()
        completedSessionsJob?.cancel()
        gameProfileJob?.cancel()
        stopAmbientSound()
        super.onCleared()
    }
}