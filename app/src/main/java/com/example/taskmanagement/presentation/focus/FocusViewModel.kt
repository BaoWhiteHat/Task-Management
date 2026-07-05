package com.example.taskmanagement.presentation.focus

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.taskmanagement.data.local.AppDatabase
import com.example.taskmanagement.data.local.models.FocusSession
import com.example.taskmanagement.data.local.models.GameProfile
import com.example.taskmanagement.data.local.models.ProfileBackground
import com.example.taskmanagement.data.local.models.ProfileSound
import com.example.taskmanagement.presentation.focus.utils.SoundPlayer
import com.example.taskmanagement.presentation.loot.rollLootItem
import com.example.taskmanagement.presentation.shop.shopTomes
import com.example.taskmanagement.presentation.shop.shopBackgrounds
import com.example.taskmanagement.presentation.shop.GuardianItemIds
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class FocusViewModel : ViewModel() {

    companion object {
        // LUU Y: doi thanh false truoc khi demo/nop bai (true = timer chay nhanh 50x de test)
        private const val FAST_TEST_MODE = true
        private const val PROFILE_ID = 1
        private const val DEFAULT_SOUND_ID = "rain"
        private const val RETREAT_XP_PENALTY = 15
        private const val RETREAT_COIN_PENALTY = 5
        private val GUARDIAN_ITEM_IDS = setOf(
            GuardianItemIds.STREAK_SHIELD,
            GuardianItemIds.FOCUS_POTION,
            GuardianItemIds.TOME_SEAL,
            GuardianItemIds.LOOT_MAGNET,
            GuardianItemIds.LOOT_MAGNET_ACTIVE
        )

        private val TIMER_TICK_MS: Long
            get() = if (FAST_TEST_MODE) 20L else 1000L
    }

    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var completedSessionsJob: Job? = null
    private var gameProfileJob: Job? = null
    private var profileTomesJob: Job? = null
    private var profileSoundsJob: Job? = null
    private var profileBackgroundsJob: Job? = null
    private var guardianItemsJob: Job? = null
    private var ambientPlayer: MediaPlayer? = null
    private var selectedTaskId: Int? = null
    private var retreatInProgress = false

    private var rewardXpMultiplier: Float = 1f
    private var rewardCoinMultiplier: Float = 1f

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
        val database = AppDatabase.getDatabase(appContext)
        val dao = database.gameProfileDao()

        gameProfileJob = viewModelScope.launch {
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
            dao.getProfile().collect { profile ->
                profile?.let {
                    _uiState.update { state ->
                        state.copy(gameProfile = it)
                    }
                }
            }
        }

        profileSoundsJob = viewModelScope.launch {
            database.profileSoundDao().observeUnlockedSounds(PROFILE_ID).collect { soundIds ->
                val unlockedIds = soundIds.toSet()
                _uiState.update { state ->
                    state.copy(
                        unlockedSoundIds = unlockedIds,
                        selectedSoundId = state.selectedSoundId
                            ?.takeIf { it in unlockedIds }
                            ?: DEFAULT_SOUND_ID.takeIf { it in unlockedIds }
                    )
                }
            }
        }

        profileBackgroundsJob = viewModelScope.launch {
            database.profileBackgroundDao()
                .observeUnlockedBackgrounds(PROFILE_ID)
                .collect { backgroundIds ->
                    _uiState.update {
                        it.copy(unlockedBackgroundIds = backgroundIds.toSet())
                    }
                }
        }

        profileTomesJob = viewModelScope.launch {
            database.profileTomeDao().observeAll(PROFILE_ID).collect { tomes ->
                val tomeCounts = tomes.associate { it.tomeId to it.count }
                _uiState.update { state ->
                    state.copy(
                        tomeCounts = tomeCounts,
                        armedTomeId = state.armedTomeId?.takeIf {
                            (tomeCounts[it] ?: 0) > 0
                        }
                    )
                }
            }
        }

        guardianItemsJob = viewModelScope.launch {
            database.lootInventoryDao().getOwned().collect { items ->
                val counts = items
                    .filter { it.itemId in GUARDIAN_ITEM_IDS }
                    .associate { it.itemId to it.count }
                _uiState.update { it.copy(guardianItemCounts = counts) }
            }
        }
    }

    fun setRewardMultipliers(xpMultiplier: Float, coinMultiplier: Float) {
        rewardXpMultiplier = xpMultiplier
        rewardCoinMultiplier = coinMultiplier
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
        _uiState.update {
            if (soundId in it.unlockedSoundIds) it.copy(selectedSoundId = soundId) else it
        }
    }

    // Arm a tome for the next battle. Tapping the armed tome again clears it.
    fun selectTome(tomeId: String) {
        _uiState.update { state ->
            val selectedId = when {
                state.armedTomeId == tomeId -> null
                (state.tomeCounts[tomeId] ?: 0) > 0 -> tomeId
                else -> state.armedTomeId
            }
            state.copy(armedTomeId = selectedId)
        }
    }

    fun unlockSound(context: Context, sound: AmbientSound) {
        val profile = _uiState.value.gameProfile ?: return
        if (profile.coins < sound.price) return
        if (sound.id in _uiState.value.unlockedSoundIds) return

        val appContext = context.applicationContext
        val database = AppDatabase.getDatabase(appContext)

        viewModelScope.launch {
            database.withTransaction {
                val dao = database.gameProfileDao()
                val soundDao = database.profileSoundDao()
                val fresh = dao.getProfile().first() ?: return@withTransaction
                if (fresh.coins < sound.price) return@withTransaction
                if (soundDao.isUnlocked(PROFILE_ID, sound.id)) return@withTransaction

                soundDao.unlock(ProfileSound(PROFILE_ID, sound.id))
                dao.updateProfile(
                    fresh.copy(coins = (fresh.coins - sound.price).coerceAtLeast(0))
                )
            }
            _uiState.update { state ->
                state.copy(
                    unlockedSoundIds = state.unlockedSoundIds + sound.id,
                    selectedSoundId = sound.id
                )
            }
        }
    }

    // Timer controls

    fun start(
        context: Context,
        selectedTaskId: Int? = null,
        taskTitle: String = ""
    ) {
        if (_uiState.value.isRunning) return

        this.selectedTaskId = selectedTaskId?.takeIf { it > 0 }
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

    private fun resetBattleState(feedbackMessage: String? = null) {
        timerJob?.cancel()
        timerJob = null
        stopAmbientSound()

        val state = _uiState.value
        val preset = state.selectedPreset

        _uiState.update {
            it.copy(
                phase = FocusPhase.STUDY,
                timeLeft = preset.studySeconds,
                isRunning = false,
                showBreakActivityPopup = false,
                lootDrop = null,
                bonusLootDrop = null,
                breakActivitySuggestion = null,
                showRetreatDialog = false,
                showSessionCompletePopup = false,
                feedbackMessage = feedbackMessage
            )
        }
    }

    fun requestRetreat() {
        if (retreatInProgress) return
        _uiState.update { it.copy(showRetreatDialog = true) }
    }

    fun dismissRetreatDialog() {
        _uiState.update { it.copy(showRetreatDialog = false) }
    }

    fun retreat(context: Context, useFocusPotion: Boolean, onRetreated: () -> Unit) {
        if (retreatInProgress) return
        retreatInProgress = true
        val earlyRetreat = _uiState.value.isEarlyRetreat
        timerJob?.cancel()
        timerJob = null
        stopAmbientSound()

        val database = AppDatabase.getDatabase(context.applicationContext)
        viewModelScope.launch {
            var potionUsed = false
            var penaltyApplied = false

            database.withTransaction {
                val profileDao = database.gameProfileDao()
                val freshProfile = profileDao.getProfile().first() ?: return@withTransaction

                if (earlyRetreat) {
                    potionUsed = useFocusPotion &&
                        database.lootInventoryDao().consumeOne(GuardianItemIds.FOCUS_POTION)
                    if (!potionUsed) {
                        profileDao.updateProfile(
                            freshProfile.copy(
                                xp = (freshProfile.xp - RETREAT_XP_PENALTY).coerceAtLeast(0),
                                coins = (freshProfile.coins - RETREAT_COIN_PENALTY).coerceAtLeast(0),
                                streakDays = 0
                            )
                        )
                        penaltyApplied = true
                    }
                }
            }

            val feedback = when {
                potionUsed -> "Focus Potion softened the retreat penalty."
                penaltyApplied -> "Retreat penalty applied."
                else -> "Safe retreat. No penalty applied."
            }
            resetBattleState(feedback)
            retreatInProgress = false
            onRetreated()
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    fun dismissLevelUp() {
        _uiState.update { it.copy(levelUp = null) }
    }

    // Session complete popup (sau break)

    fun dismissSessionCompletePopup() {
        _uiState.update { it.copy(showSessionCompletePopup = false) }
    }

    fun continueAfterBreak(
        context: Context,
        selectedTaskId: Int? = null,
        taskTitle: String = ""
    ) {
        _uiState.update { it.copy(showSessionCompletePopup = false) }
        start(context, selectedTaskId, taskTitle)
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
        _uiState.update {
            it.copy(showBreakActivityPopup = false, lootDrop = null, bonusLootDrop = null)
        }
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

        val completedBreakNaturally =
            currentState.phase == FocusPhase.BREAK && completedNaturally

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
                else it.breakActivitySuggestion,
                showSessionCompletePopup = completedBreakNaturally
            )
        }
    }

    // XP & Coins

    private fun rewardXpAndCoins(context: Context, preset: FocusPreset) {
        val appContext = context.applicationContext
        val database = AppDatabase.getDatabase(appContext)
        val dao = database.gameProfileDao()
        val tomeDao = database.profileTomeDao()

        viewModelScope.launch {
            // Apply an armed tome's buff, then consume one (only on a win, which this is).
            val armedId = _uiState.value.armedTomeId
            val tome = if (armedId != null) shopTomes.firstOrNull { it.id == armedId } else null
            var originalProfile: GameProfile? = null
            var resultingLevel = 0
            var usedTome = false
            var tomePreserved = false
            var streakProtected = false
            var bonusDrop: com.example.taskmanagement.presentation.loot.LootItem? = null
            val normalDrop = rollLootItem()

            database.withTransaction {
                val profile = dao.getProfile().first() ?: return@withTransaction
                val lootDao = database.lootInventoryDao()
                originalProfile = profile
                val tomeAvailable = tome != null &&
                    (tomeDao.getTomeCount(PROFILE_ID, tome.id) ?: 0) > 0
                if (tomeAvailable) {
                    tomePreserved = lootDao.consumeOne(GuardianItemIds.TOME_SEAL)
                    usedTome = tomePreserved || tomeDao.decrement(PROFILE_ID, tome!!.id)
                }

                val xpReward = (preset.xpReward * (if (usedTome) tome!!.xpMult else 1f)).toInt()
                val coinReward = (preset.coinReward * (if (usedTome) tome!!.coinMult else 1f)).toInt()

                var newLevel = profile.level
                var newXp = profile.xp + xpReward
                var needed = 100 + (newLevel - 1) * 50            // = xpForNextLevel
                while (newXp >= needed) {
                    newXp -= needed
                    newLevel += 1
                    needed = 100 + (newLevel - 1) * 50
                }
                resultingLevel = newLevel

                val today = LocalDate.now().toString()
                val yesterday = LocalDate.now().minusDays(1).toString()
                val twoDaysAgo = LocalDate.now().minusDays(2).toString()
                val newStreak = when (profile.lastFocusDate) {
                    today -> profile.streakDays.coerceAtLeast(1)
                    yesterday -> profile.streakDays + 1
                    twoDaysAgo -> {
                        streakProtected = profile.streakDays > 0 &&
                            lootDao.consumeOne(GuardianItemIds.STREAK_SHIELD)
                        if (streakProtected) profile.streakDays + 1 else 1
                    }
                    else -> 1
                }

                dao.updateProfile(
                    profile.copy(
                        xp = newXp,
                        level = newLevel,
                        coins = profile.coins + coinReward,
                        lastFocusDate = today,
                        streakDays = newStreak,
                        bestStreak = maxOf(profile.bestStreak, newStreak),
                        totalSessions = profile.totalSessions + 1
                    )
                )

                lootDao.addItem(normalDrop.id, 1)
                if (lootDao.consumeOne(GuardianItemIds.LOOT_MAGNET_ACTIVE)) {
                    bonusDrop = rollLootItem()
                    lootDao.addItem(bonusDrop!!.id, 1)
                }
            }

            val profile = originalProfile ?: return@launch
            val leveledUp = resultingLevel > profile.level

            if (usedTome && !tomePreserved) {
                _uiState.update { it.copy(armedTomeId = null) }
            }

            if (leveledUp) {
                _uiState.update {
                    it.copy(
                        levelUp = LevelUpInfo(
                            resultingLevel,
                            profile.copy(level = resultingLevel).title
                        )
                    )
                }
            }

            // Loot drop — written to its own table (loot_inventory), independent of
            // the profile write above (different table, so no stale-snapshot clash).
            // Shown as a chest in the break dialog.
            val feedbackMessages = buildList {
                if (streakProtected) add("Your streak was protected by a Shield.")
                if (tomePreserved) add("Tome Seal preserved your armed tome.")
                if (bonusDrop != null) add("Loot Magnet attracted bonus loot!")
            }
            _uiState.update {
                it.copy(
                    lootDrop = normalDrop,
                    bonusLootDrop = bonusDrop,
                    feedbackMessage = feedbackMessages
                        .takeIf { messages -> messages.isNotEmpty() }
                        ?.joinToString("\n")
                )
            }
        }
    }

    // Ambient sound

    private fun startAmbientSound(context: Context) {
        stopAmbientSound()
        val soundId = _uiState.value.selectedSoundId ?: return
        if (soundId !in _uiState.value.unlockedSoundIds) return

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
            val database = AppDatabase.getDatabase(appContext)
            val validTaskId = selectedTaskId?.let { database.taskDao().findExistingId(it) }

            database.focusSessionDao()
                .insertFocusSession(
                    FocusSession(
                        taskId = validTaskId,
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
        profileTomesJob?.cancel()
        profileSoundsJob?.cancel()
        profileBackgroundsJob?.cancel()
        guardianItemsJob?.cancel()
        stopAmbientSound()
        super.onCleared()
    }
}
