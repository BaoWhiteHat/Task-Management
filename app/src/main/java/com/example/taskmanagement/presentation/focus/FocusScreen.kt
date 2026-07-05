package com.example.taskmanagement.presentation.focus

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

private enum class FocusPage {
    SETUP,
    SESSION
}

@Composable
fun FocusScreen(
    taskId: Int? = null,
    taskTitle: String = "",
    taskTag: String = "",
    taskPriority: String = "",
    onNavigateBack: () -> Unit = {},
    onOpenForest: () -> Unit = {},
    viewModel: FocusViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    val encounter = remember(taskTitle, taskTag, taskPriority) {
        encounterFromTask(taskTitle, taskTag, taskPriority)
    }

    LaunchedEffect(Unit) {
        viewModel.loadTodayCompletedSessions(context)
        viewModel.loadGameProfile(context)
    }

    var currentPage by rememberSaveable {
        mutableStateOf(FocusPage.SETUP)
    }

    val minutes = state.timeLeft / 60
    val seconds = state.timeLeft % 60
    val timeText = "%02d:%02d".format(minutes, seconds)

    BackHandler(
        enabled = currentPage == FocusPage.SESSION && !state.showSessionCompletePopup
    ) {
        if (state.showRetreatDialog) {
            viewModel.dismissRetreatDialog()
        } else {
            viewModel.requestRetreat()
        }
    }

    LaunchedEffect(state.feedbackMessage) {
        state.feedbackMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearFeedback()
        }
    }

    when (currentPage) {
        FocusPage.SETUP -> {
            FocusSetupScreen(
                taskTitle = taskTitle,
                state = state,
                onNavigateBack = onNavigateBack,
                onSelectPreset = viewModel::selectPreset,
                onSelectSound = viewModel::selectSound,
                onSelectTome = viewModel::selectTome,
                onOpenForest = onOpenForest,
                onStartSession = {
                    currentPage = FocusPage.SESSION
                    viewModel.start(
                        context = context,
                        selectedTaskId = taskId,
                        taskTitle = taskTitle
                    )
                }
            )
        }

        FocusPage.SESSION -> {
            FocusBattleScreen(
                state = state,
                timeText = timeText,
                encounter = encounter,
                onStart = {
                    viewModel.start(
                        context = context,
                        selectedTaskId = taskId,
                        taskTitle = taskTitle
                    )
                },
                onPause = viewModel::pause,
                onRequestRetreat = viewModel::requestRetreat,
                onRetreatWithPotion = {
                    viewModel.retreat(context, useFocusPotion = true) {
                        currentPage = FocusPage.SETUP
                    }
                },
                onRetreatWithoutPotion = {
                    viewModel.retreat(context, useFocusPotion = false) {
                        currentPage = FocusPage.SETUP
                    }
                },
                onDismissRetreat = viewModel::dismissRetreatDialog,
                onSkip = viewModel::skipPhase
            )
        }
    }

    val suggestion = state.breakActivitySuggestion

    if (state.showBreakActivityPopup && suggestion != null && state.levelUp == null) {
        BreakActivityDialog(
            suggestion = suggestion,
            lootDrop = state.lootDrop,
            bonusLootDrop = state.bonusLootDrop,
            onDismiss = viewModel::dismissBreakActivityPopup,
            onAnotherIdea = viewModel::randomizeBreakActivity,
            onStartBreak = {
                viewModel.dismissBreakActivityPopup()
                viewModel.start(
                    context = context,
                    selectedTaskId = taskId,
                    taskTitle = taskTitle
                )
            }
        )
    }

    if (state.showSessionCompletePopup) {
        SessionCompleteDialog(
            onContinue = {
                viewModel.continueAfterBreak(
                    context = context,
                    selectedTaskId = taskId,
                    taskTitle = taskTitle
                )
            },
            onLeave = {
                viewModel.dismissSessionCompletePopup()
                onNavigateBack()
            }
        )
    }

    state.levelUp?.let { info ->
        LevelUpDialog(
            level = info.level,
            title = info.title,
            onDismiss = viewModel::dismissLevelUp
        )
    }
}
