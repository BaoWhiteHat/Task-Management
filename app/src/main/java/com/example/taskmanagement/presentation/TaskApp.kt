package com.example.taskmanagement.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.taskmanagement.presentation.components.AnalyticsTopAppBar
import com.example.taskmanagement.presentation.components.CalendarTopAppBar
import com.example.taskmanagement.presentation.components.HomeTopAppBar
import com.example.taskmanagement.presentation.components.MyTaskTopAppBar
import com.example.taskmanagement.presentation.components.NewTaskTopAppBar
import com.example.taskmanagement.presentation.navigation.BottomNavigationBar
import com.example.taskmanagement.presentation.navigation.Screen
import com.example.taskmanagement.presentation.navigation.TaskNavigation

@Composable
fun TaskApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
        ?: Screen.Home.route
    val shouldShowBottomBar = currentRoute != Screen.NewTask.route
    Scaffold(
        topBar = {
            when(currentRoute){
                Screen.Home.route -> HomeTopAppBar()
                Screen.MyTasks.route -> MyTaskTopAppBar()
                Screen.Calendar.route -> CalendarTopAppBar()
                Screen.Analytics.route -> AnalyticsTopAppBar()
                Screen.NewTask.route -> NewTaskTopAppBar { navController.popBackStack() }
            }
        },
        bottomBar = {
            AnimatedVisibility(shouldShowBottomBar) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate ={route ->
                        navController.navigateToSingleTop(route)
                    },
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        TaskNavigation(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }


}


fun NavHostController.navigateToSingleTop(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}