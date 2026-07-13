package com.example.taskmanagement

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.SideEffect
import com.example.taskmanagement.presentation.TaskApp
import com.example.taskmanagement.presentation.appearance.AppearancePreferences
import com.example.taskmanagement.presentation.ui.theme.TaskManagementTheme
import com.example.taskmanagement.reminder.ReminderScheduler
import com.example.taskmanagement.utils.Utils

class MainActivity : ComponentActivity() {

    private val notifPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.createNotificationChannel(this)
        ReminderScheduler.ensureChannel(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            val appearancePreferences = remember { AppearancePreferences(applicationContext) }
            val appearanceState by appearancePreferences.state.collectAsState()
            val darkTheme = appearanceState.themeMode.resolveDarkTheme()
            SideEffect {
                enableEdgeToEdge(
                    statusBarStyle = if (darkTheme) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    },
                    navigationBarStyle = if (darkTheme) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    }
                )
            }

            TaskManagementTheme(
                darkTheme = darkTheme,
                textSize = appearanceState.textSize,
                fontStyle = appearanceState.fontStyle
            ) {
                TaskApp(
                    appearanceState = appearanceState,
                    onThemeModeChange = appearancePreferences::setThemeMode,
                    onTextSizeChange = appearancePreferences::setTextSize,
                    onFontStyleChange = appearancePreferences::setFontStyle
                )
            }
        }
    }
}
