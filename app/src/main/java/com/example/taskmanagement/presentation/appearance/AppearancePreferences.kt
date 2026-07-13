package com.example.taskmanagement.presentation.appearance

import android.content.Context
import android.content.SharedPreferences
import com.example.taskmanagement.presentation.ui.theme.AppThemeMode
import com.example.taskmanagement.presentation.ui.theme.AppearanceFontStyle
import com.example.taskmanagement.presentation.ui.theme.AppearanceState
import com.example.taskmanagement.presentation.ui.theme.AppearanceTextSize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val APP_PREFERENCES = "task_management_preferences"
private const val APPEARANCE_THEME_KEY = "appearance_theme"
private const val APPEARANCE_TEXT_SIZE_KEY = "appearance_text_size"
private const val APPEARANCE_FONT_STYLE_KEY = "appearance_font_style"
private const val LEGACY_THEME_MODE_KEY = "theme_mode"

class AppearancePreferences(context: Context) {
    private val preferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(readState())
    val state: StateFlow<AppearanceState> = _state.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        update(_state.value.copy(themeMode = mode)) {
            putString(APPEARANCE_THEME_KEY, mode.storedValue)
            putString(LEGACY_THEME_MODE_KEY, mode.storedValue)
        }
    }

    fun setTextSize(textSize: AppearanceTextSize) {
        update(_state.value.copy(textSize = textSize)) {
            putString(APPEARANCE_TEXT_SIZE_KEY, textSize.storedValue)
        }
    }

    fun setFontStyle(fontStyle: AppearanceFontStyle) {
        update(_state.value.copy(fontStyle = fontStyle)) {
            putString(APPEARANCE_FONT_STYLE_KEY, fontStyle.storedValue)
        }
    }

    private fun update(
        state: AppearanceState,
        write: SharedPreferences.Editor.() -> SharedPreferences.Editor
    ) {
        _state.value = state
        preferences.edit().write().apply()
    }

    private fun readState(): AppearanceState {
        val themeValue = preferences.getString(APPEARANCE_THEME_KEY, null)
            ?: preferences.getString(LEGACY_THEME_MODE_KEY, null)
            ?: AppThemeMode.DARK.storedValue
        val themeMode = AppThemeMode.fromStoredValue(themeValue)

        if (!preferences.contains(APPEARANCE_THEME_KEY)) {
            preferences.edit()
                .putString(APPEARANCE_THEME_KEY, themeMode.storedValue)
                .putString(LEGACY_THEME_MODE_KEY, themeMode.storedValue)
                .apply()
        }

        return AppearanceState(
            themeMode = themeMode,
            textSize = AppearanceTextSize.fromStoredValue(
                preferences.getString(APPEARANCE_TEXT_SIZE_KEY, AppearanceTextSize.DEFAULT.storedValue)
            ),
            fontStyle = AppearanceFontStyle.fromStoredValue(
                preferences.getString(APPEARANCE_FONT_STYLE_KEY, AppearanceFontStyle.PIXEL.storedValue)
            )
        )
    }
}
