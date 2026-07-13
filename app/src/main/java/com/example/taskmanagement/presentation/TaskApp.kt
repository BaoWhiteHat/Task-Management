package com.example.taskmanagement.presentation

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.taskmanagement.presentation.components.NewTaskTopAppBar
import com.example.taskmanagement.presentation.focus.StoryMode
import com.example.taskmanagement.presentation.focus.StoryScreen
import com.example.taskmanagement.presentation.navigation.BottomNavigationBar
import com.example.taskmanagement.presentation.navigation.Screen
import com.example.taskmanagement.presentation.navigation.TaskNavigation
import com.example.taskmanagement.presentation.onboarding.LegendIntroDialog
import com.example.taskmanagement.presentation.ui.theme.AppThemeMode
import com.example.taskmanagement.presentation.ui.theme.AppearanceFontStyle
import com.example.taskmanagement.presentation.ui.theme.AppearanceState
import com.example.taskmanagement.presentation.ui.theme.AppearanceTextSize

private const val INTRO_PREFERENCES = "legend_onboarding"
private const val HAS_SEEN_LEGEND_INTRO = "has_seen_legend_intro"
private const val INTRO_PROMPT = "prompt"
private const val INTRO_LORE = "lore"
private const val INTRO_COMPLETE = "complete"

@Composable
fun TaskApp(
    modifier: Modifier = Modifier,
    appearanceState: AppearanceState = AppearanceState(),
    onThemeModeChange: (AppThemeMode) -> Unit = {},
    onTextSizeChange: (AppearanceTextSize) -> Unit = {},
    onFontStyleChange: (AppearanceFontStyle) -> Unit = {}
) {
    val context = LocalContext.current
    val preferences = remember(context) {
        context.getSharedPreferences(INTRO_PREFERENCES, Context.MODE_PRIVATE)
    }
    var introState by rememberSaveable {
        mutableStateOf(
            if (preferences.getBoolean(HAS_SEEN_LEGEND_INTRO, false)) {
                INTRO_COMPLETE
            } else {
                INTRO_PROMPT
            }
        )
    }

    fun finishLegendIntro() {
        preferences.edit().putBoolean(HAS_SEEN_LEGEND_INTRO, true).apply()
        introState = INTRO_COMPLETE
    }

    if (introState == INTRO_LORE) {
        StoryScreen(
            mode = StoryMode.LORE,
            onNavigateBack = { introState = INTRO_PROMPT },
            onBegin = ::finishLegendIntro
        )
        return
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
        ?: Screen.Home.route
    val shouldShowBottomBar = currentRoute != Screen.NewTask.route
            && !currentRoute.startsWith(Screen.Focus.route)
            && currentRoute != Screen.Forest.route
            && currentRoute != Screen.Achievements.route
            && currentRoute != Screen.Notebook.route
            && currentRoute != Screen.Shop.route
            && currentRoute != Screen.Appearance.route

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                when {
                    currentRoute == Screen.NewTask.route -> {
                        NewTaskTopAppBar { navController.popBackStack() }
                    }
                    else -> {}
                }
            },
            bottomBar = {
                AnimatedVisibility(shouldShowBottomBar) {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigateToSingleTop(route)
                        },
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            TaskNavigation(
                navController = navController,
                appearanceState = appearanceState,
                onThemeModeChange = onThemeModeChange,
                onTextSizeChange = onTextSizeChange,
                onFontStyleChange = onFontStyleChange,
                modifier = Modifier.padding(paddingValues)
            )
        }

        if (introState == INTRO_PROMPT) {
            LegendIntroDialog(
                onReadHistory = { introState = INTRO_LORE },
                onSkip = ::finishLegendIntro
            )
        }
    }
}

fun NavHostController.navigateToSingleTop(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
