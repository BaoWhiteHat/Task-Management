package com.example.taskmanagement.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

//  Material Color Schemes
private val AuroraLightScheme = lightColorScheme(
    primary            = AuroraGreen500,
    onPrimary          = Color.White,
    primaryContainer   = AuroraGreen50,
    onPrimaryContainer = AuroraGreen900,

    secondary          = AuroraAmber,
    onSecondary        = Color.White,
    secondaryContainer = AuroraAmberLight,
    onSecondaryContainer = Color(0xFF5C3D10),

    tertiary           = TagPersonal,
    onTertiary         = Color.White,
    tertiaryContainer  = TagPersonalBg,
    onTertiaryContainer = Color(0xFF4B1528),

    background         = LightBackground,
    onBackground       = LightOnBackground,

    surface            = LightSurface,
    onSurface          = LightOnSurface,
    surfaceVariant     = LightSurfaceVar,
    onSurfaceVariant   = LightOnSurfaceVar,

    outline            = LightOutline,
    outlineVariant     = LightOutlineVar,

    error              = SemanticError,
    onError            = Color.White,
    errorContainer     = SemanticErrorBg,
    onErrorContainer   = Color(0xFF501313),
)

private val MidnightDarkScheme = darkColorScheme(
    primary            = MidnightIndigo500,
    onPrimary          = Color.White,
    primaryContainer   = MidnightIndigo700,
    onPrimaryContainer = MidnightIndigo50,

    secondary          = MidnightCoral,
    onSecondary        = Color.White,
    secondaryContainer = MidnightCoralDark,
    onSecondaryContainer = Color(0xFFFFB4A0),

    tertiary           = TagPersonal,
    onTertiary         = Color.White,
    tertiaryContainer  = Color(0xFF3D1528),
    onTertiaryContainer = Color(0xFFF4C0D1),

    background         = DarkBackground,
    onBackground       = DarkOnBackground,

    surface            = DarkSurface,
    onSurface          = DarkOnSurface,
    surfaceVariant     = DarkSurfaceVar,
    onSurfaceVariant   = DarkOnSurfaceVar,

    outline            = DarkOutline,
    outlineVariant     = DarkOutlineVar,

    error              = Color(0xFFFF6B6B),
    onError            = Color.White,
    errorContainer     = Color(0xFF3D1515),
    onErrorContainer   = Color(0xFFFFB4B4),
)

@Immutable
data class TaskExtendedColors(
    // Priority
    val priorityHigh: Color,
    val priorityHighBg: Color,
    val priorityMedium: Color,
    val priorityMediumBg: Color,
    val priorityLow: Color,
    val priorityLowBg: Color,

    // Tags
    val tagWork: Color,
    val tagWorkBg: Color,
    val tagPersonal: Color,
    val tagPersonalBg: Color,
    val tagHealth: Color,
    val tagHealthBg: Color,
    val tagOther: Color,
    val tagOtherBg: Color,

    // Semantic
    val success: Color,
    val successBg: Color,
    val warning: Color,
    val warningBg: Color,

    // UI đặc biệt
    val subText: Color,
    val cardBg: Color,
    val accentColor: Color,
)

val LightExtendedColors = TaskExtendedColors(
    priorityHigh     = PriorityHigh,
    priorityHighBg   = PriorityHighBg,
    priorityMedium   = PriorityMedium,
    priorityMediumBg = PriorityMediumBg,
    priorityLow      = PriorityLow,
    priorityLowBg    = PriorityLowBg,

    tagWork        = TagWork,
    tagWorkBg      = TagWorkBg,
    tagPersonal    = TagPersonal,
    tagPersonalBg  = TagPersonalBg,
    tagHealth      = TagHealth,
    tagHealthBg    = TagHealthBg,
    tagOther       = TagOther,
    tagOtherBg     = TagOtherBg,

    success   = SemanticSuccess,
    successBg = SemanticSuccessBg,
    warning   = SemanticWarning,
    warningBg = SemanticWarningBg,

    subText     = LightSubText,
    cardBg      = LightCardBg,
    accentColor = AuroraAmber,
)

val DarkExtendedColors = TaskExtendedColors(
    priorityHigh     = PriorityHighDark,
    priorityHighBg   = PriorityHighBgDark,
    priorityMedium   = PriorityMediumDark,
    priorityMediumBg = PriorityMediumBgDark,
    priorityLow      = PriorityLowDark,
    priorityLowBg    = PriorityLowBgDark,

    tagWork        = Color(0xFF9BA8F8),
    tagWorkBg      = Color(0xFF1E2350),
    tagPersonal    = Color(0xFFED93B1),
    tagPersonalBg  = Color(0xFF3D1528),
    tagHealth      = Color(0xFF4DBD94),
    tagHealthBg    = Color(0xFF0F2E22),
    tagOther       = Color(0xFFB4B2A9),
    tagOtherBg     = Color(0xFF2A2A28),

    success   = SemanticSuccess,
    successBg = Color(0xFF0F2E22),
    warning   = SemanticWarning,
    warningBg = Color(0xFF3D3410),

    subText     = DarkSubText,
    cardBg      = DarkCardBg,
    accentColor = MidnightCoral,
)

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

//  Theme Composable
@Composable
fun TaskManagementTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) MidnightDarkScheme else AuroraLightScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    androidx.compose.runtime.CompositionLocalProvider(
        LocalExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

//  Helper accessor
//  TaskTheme.colors.priorityHigh
//  TaskTheme.colors.tagWork

object TaskTheme {
    val colors: TaskExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColors.current
}