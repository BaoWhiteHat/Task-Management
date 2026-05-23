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
    onNavigateBack: () -> Unit = {},
    viewModel: FocusViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTodayCompletedSessions(context)
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
                completedSessions = state.completedStudySessions,
                selectedIndex = state.selectedPresetIndex,
                isRunning = state.isRunning,
                onNavigateBack = onNavigateBack,
                onSelectPreset = viewModel::selectPreset,
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
            FocusSessionScreen(
                state = state,
                timeText = timeText,
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
                    viewModel.reset()
                    currentPage = FocusPage.SETUP
                },
                onSkip = viewModel::skipPhase
            )
        }
    }

    val suggestion = state.breakActivitySuggestion

    if (state.showBreakActivityPopup && suggestion != null) {
        BreakActivityDialog(
            suggestion = suggestion,
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
}