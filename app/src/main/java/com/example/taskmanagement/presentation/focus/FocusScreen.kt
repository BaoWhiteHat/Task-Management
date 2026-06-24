package com.example.taskmanagement.presentation.focus

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
                onBackToSetup = {
                    viewModel.pause()
                    currentPage = FocusPage.SETUP
                },
                onStart = {
                    viewModel.start(
                        context = context,
                        taskTitle = taskTitle
                    )
                },
                onPause = viewModel::pause,
                onReset = {
                    viewModel.requestReset()
                },
                onConfirmReset = {
                    viewModel.reset(context)
                    currentPage = FocusPage.SETUP
                },
                onDismissPenalty = viewModel::dismissPenaltyWarning,
                onSkip = viewModel::skipPhase
            )
        }
    }

    val suggestion = state.breakActivitySuggestion

    if (state.showBreakActivityPopup && suggestion != null) {
        BreakActivityDialog(
            suggestion = suggestion,
            lootDrop = state.lootDrop,
            onDismiss = viewModel::dismissBreakActivityPopup,
            onAnotherIdea = viewModel::randomizeBreakActivity,
            onStartBreak = {
                viewModel.dismissBreakActivityPopup()
                viewModel.start(
                    context = context,
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
                    taskTitle = taskTitle
                )
            },
            onLeave = {
                viewModel.dismissSessionCompletePopup()
                onNavigateBack()
            }
        )
    }
}