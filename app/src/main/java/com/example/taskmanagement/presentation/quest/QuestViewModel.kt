package com.example.taskmanagement.presentation.quest

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.taskmanagement.data.local.AppDatabase
import com.example.taskmanagement.data.local.models.FocusSession
import com.example.taskmanagement.data.local.models.QuestClaim
import com.example.taskmanagement.presentation.loot.LootItem
import com.example.taskmanagement.presentation.loot.rollLootItemOfRarity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.IsoFields

data class QuestUi(
    val quest: Quest,
    val progress: Int,
    val claimed: Boolean,
    val claimable: Boolean
)

data class TrackState(
    val done: Int,
    val total: Int,
    val bonus: TrackBonus,
    val bonusClaimed: Boolean,
    val bonusClaimable: Boolean
)

data class ClaimedReward(
    val coins: Int,
    val item: LootItem?
)

data class QuestBoardState(
    val daily: List<QuestUi> = emptyList(),
    val weekly: List<QuestUi> = emptyList(),
    val dailyTrack: TrackState? = null,
    val weeklyTrack: TrackState? = null
)

class QuestViewModel : ViewModel() {

    private val _board = MutableStateFlow(QuestBoardState())
    val board = _board.asStateFlow()

    private val _coins = MutableStateFlow(0)
    val coins = _coins.asStateFlow()

    private val _claimedReward = MutableStateFlow<ClaimedReward?>(null)
    val claimedReward = _claimedReward.asStateFlow()

    fun dismissClaimedReward() { _claimedReward.value = null }

    val anyClaimable = board
        .map { b ->
            b.daily.any { it.claimable } || b.weekly.any { it.claimable } ||
                    (b.dailyTrack?.bonusClaimable == true) || (b.weeklyTrack?.bonusClaimable == true)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private var started = false

    fun start(context: Context) {
        if (started) return
        started = true
        val db = AppDatabase.getDatabase(context.applicationContext)
        val dailyKey = periodKey(QuestPeriod.DAILY)
        val weeklyKey = periodKey(QuestPeriod.WEEKLY)

        viewModelScope.launch {
            db.gameProfileDao().createProfile()
        }

        viewModelScope.launch {
            combine(
                db.focusSessionDao().getAllFocusSessions(),
                db.questClaimDao().claimedForPeriod(dailyKey),
                db.questClaimDao().claimedForPeriod(weeklyKey)
            ) { sessions, dailyClaimed, weeklyClaimed ->
                buildBoard(sessions, dailyClaimed.toSet(), weeklyClaimed.toSet())
            }.collect { _board.value = it }
        }

        viewModelScope.launch {
            db.gameProfileDao().getProfile().collect { _coins.value = it?.coins ?: 0 }
        }
    }

    private fun buildBoard(
        sessions: List<FocusSession>,
        dailyClaimed: Set<String>,
        weeklyClaimed: Set<String>
    ): QuestBoardState {
        fun mk(q: Quest): QuestUi {
            val progress = progressFor(q, sessions)
            val claimedSet = if (q.period == QuestPeriod.DAILY) dailyClaimed else weeklyClaimed
            val claimed = q.id in claimedSet
            return QuestUi(q, progress, claimed, progress >= q.target && !claimed)
        }

        val daily = questsFor(QuestPeriod.DAILY).map(::mk)
        val weekly = questsFor(QuestPeriod.WEEKLY).map(::mk)

        return QuestBoardState(
            daily = daily,
            weekly = weekly,
            dailyTrack = trackState(daily, QuestPeriod.DAILY, dailyClaimed),
            weeklyTrack = trackState(weekly, QuestPeriod.WEEKLY, weeklyClaimed)
        )
    }

    private fun trackState(
        list: List<QuestUi>,
        period: QuestPeriod,
        claimedSet: Set<String>
    ): TrackState {
        val bonus = trackBonusFor(period)
        val done = list.count { it.progress >= it.quest.target }
        val total = list.size
        val bonusClaimed = bonus.id in claimedSet
        return TrackState(
            done = done,
            total = total,
            bonus = bonus,
            bonusClaimed = bonusClaimed,
            bonusClaimable = done == total && total > 0 && !bonusClaimed
        )
    }

    fun claim(context: Context, questId: String) {
        val quest = quests.firstOrNull { it.id == questId } ?: return
        val db = AppDatabase.getDatabase(context.applicationContext)
        viewModelScope.launch {
            val key = periodKey(quest.period)
            if (db.questClaimDao().isClaimed(quest.id, key) > 0) return@launch

            val sessions = db.focusSessionDao().getAllFocusSessions().first()
            if (progressFor(quest, sessions) < quest.target) return@launch

            val rolled = quest.rewardRarity?.let { rollLootItemOfRarity(it) }
            var claimed = false

            db.withTransaction {
                db.gameProfileDao().createProfile()
                if (db.questClaimDao().isClaimed(quest.id, key) > 0) return@withTransaction

                db.questClaimDao().claim(QuestClaim(questId = quest.id, periodKey = key))
                if (quest.coins > 0) db.gameProfileDao().addCoins(quest.coins)
                rolled?.let { db.lootInventoryDao().addItem(it.id, 1) }
                claimed = true
            }

            if (claimed) {
                _claimedReward.value = ClaimedReward(quest.coins, rolled)
            }
        }
    }

    fun claimBonus(context: Context, period: QuestPeriod) {
        val bonus = trackBonusFor(period)
        val db = AppDatabase.getDatabase(context.applicationContext)
        viewModelScope.launch {
            val key = periodKey(period)
            if (db.questClaimDao().isClaimed(bonus.id, key) > 0) return@launch

            val sessions = db.focusSessionDao().getAllFocusSessions().first()
            val allDone = questsFor(period).all { progressFor(it, sessions) >= it.target }
            if (!allDone) return@launch

            val rolled = rollLootItemOfRarity(bonus.rewardRarity)
            var claimed = false

            db.withTransaction {
                db.gameProfileDao().createProfile()
                if (db.questClaimDao().isClaimed(bonus.id, key) > 0) return@withTransaction

                db.questClaimDao().claim(QuestClaim(questId = bonus.id, periodKey = key))
                db.gameProfileDao().addCoins(bonus.coins)
                db.lootInventoryDao().addItem(rolled.id, 1)
                claimed = true
            }

            if (claimed) {
                _claimedReward.value = ClaimedReward(bonus.coins, rolled)
            }
        }
    }

    private fun progressFor(quest: Quest, sessions: List<FocusSession>): Int {
        val today = LocalDate.now()
        val inPeriod = sessions.filter { s ->
            when (quest.period) {
                QuestPeriod.DAILY -> s.completedDate == today
                QuestPeriod.WEEKLY -> sameIsoWeek(s.completedDate, today)
            }
        }
        return when (quest.metric) {
            QuestMetric.SESSIONS -> inPeriod.size
            QuestMetric.MINUTES -> inPeriod.sumOf { it.studyMinutes }
        }
    }

    private fun sameIsoWeek(a: LocalDate, b: LocalDate): Boolean =
        a.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == b.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) &&
                a.get(IsoFields.WEEK_BASED_YEAR) == b.get(IsoFields.WEEK_BASED_YEAR)
}
