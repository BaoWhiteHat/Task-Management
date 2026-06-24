package com.example.taskmanagement.presentation.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.taskmanagement.presentation.achievements.AchievementsScreen
import com.example.taskmanagement.presentation.analytics.AnalyticsScreen
import com.example.taskmanagement.presentation.calendar.CalendarScreen
import com.example.taskmanagement.presentation.focus.FocusScreen
import com.example.taskmanagement.presentation.focus.ForestScreen
import com.example.taskmanagement.presentation.focus.StoryScreen
import com.example.taskmanagement.presentation.shop.ShopScreen
import com.example.taskmanagement.presentation.home.TodayOverViewScreen
import com.example.taskmanagement.presentation.hub.HubScreen
import com.example.taskmanagement.presentation.my_tasks.MyTasksScreen
import com.example.taskmanagement.presentation.new_task.NewTaskScreen
import com.example.taskmanagement.presentation.navigateToSingleTop

sealed class Screen(
    val route: String,
    val icon: ImageVector? = null,
    val title: String? = null
) {
    object Home : Screen("home", Icons.Filled.Home, "Home")
    object Calendar : Screen("calendar", Icons.Default.CalendarMonth, "Calendar")
    object Analytics : Screen("analytics", Icons.Default.Analytics, "Analytics")
    object NewTask : Screen("NewTask")
    object MyTasks : Screen("MyTasks", Icons.Default.Task, title = "My tasks")

    object Focus : Screen("focus")
    object Forest : Screen("forest")
    object Achievements : Screen("achievements")
    object Story : Screen("story")
    object Shop : Screen("shop")
    object Hub : Screen("hub")
}

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onNavigate: (route: String) -> Unit
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == Screen.Home.route,
            onClick = { onNavigate(Screen.Home.route) }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Calendar") },
            label = { Text("Calendar") },
            selected = currentRoute == Screen.Calendar.route,
            onClick = { onNavigate(Screen.Calendar.route) }
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            FloatingActionButton(
                onClick = { onNavigate(Screen.NewTask.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "add tasks")
            }
        }
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Analytics, contentDescription = "Analytics") },
            label = { Text("Analytics") },
            selected = currentRoute == Screen.Analytics.route,
            onClick = { onNavigate(Screen.Analytics.route) }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Task, contentDescription = "Task") },
            label = { Text("Task") },
            selected = currentRoute == Screen.MyTasks.route,
            onClick = { onNavigate(Screen.MyTasks.route) }
        )
    }
}

@Composable
fun TaskNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(route = Screen.Home.route) {
            TodayOverViewScreen(
                modifier = modifier,
                onStartFocus = { navController.navigateToSingleTop(Screen.MyTasks.route) },
                onOpenHub = { navController.navigate(Screen.Hub.route) }
            )
        }
        composable(route = Screen.NewTask.route) { NewTaskScreen(modifier = modifier) { navController.popBackStack() } }
        composable(route = Screen.MyTasks.route) { MyTasksScreen(modifier = modifier, navController = navController) }
        composable(route = Screen.Analytics.route) { AnalyticsScreen(modifier = modifier) }
        composable(route = Screen.Calendar.route) { CalendarScreen(modifier = modifier) }

        composable(
            route = "${Screen.Focus.route}?taskTitle={taskTitle}&tag={tag}&priority={priority}",
            arguments = listOf(
                navArgument("taskTitle") { type = NavType.StringType; defaultValue = "" },
                navArgument("tag") { type = NavType.StringType; defaultValue = "" },
                navArgument("priority") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val taskTitle = backStackEntry.arguments
                ?.getString("taskTitle")
                ?.let { Uri.decode(it) }
                .orEmpty()

            val tag = backStackEntry.arguments
                ?.getString("tag")
                ?.let { Uri.decode(it) }
                .orEmpty()

            val priority = backStackEntry.arguments
                ?.getString("priority")
                ?.let { Uri.decode(it) }
                .orEmpty()

            FocusScreen(
                taskTitle = taskTitle,
                taskTag = tag,
                taskPriority = priority,
                onNavigateBack = { navController.popBackStack() },
                onOpenForest = { navController.navigate(Screen.Forest.route) }
            )
        }

        composable(route = Screen.Forest.route) {
            ForestScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenAchievements = { navController.navigate(Screen.Achievements.route) },
                onOpenStory = { navController.navigate(Screen.Story.route) },
                onOpenShop = { navController.navigate(Screen.Shop.route) }
            )
        }

        composable(route = Screen.Achievements.route) {
            AchievementsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = Screen.Story.route) {
            StoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onBegin = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Shop.route) {
            ShopScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = Screen.Hub.route) {
            HubScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenStory = { navController.navigate(Screen.Story.route) },
                onOpenAchievements = { navController.navigate(Screen.Achievements.route) },
                onOpenShop = { navController.navigate(Screen.Shop.route) },
                onStartFocus = { navController.navigateToSingleTop(Screen.MyTasks.route) }

            )
        }
    }
}