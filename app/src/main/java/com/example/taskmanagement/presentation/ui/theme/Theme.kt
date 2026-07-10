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

private val RpgBgDeep       = Color(0xFF081105)
private val RpgSurface1      = Color(0xFF101C0C)
private val RpgSurface2      = Color(0xFF162610)
private val RpgBorderSubtle  = Color(0xFF2D4A1E)
private val RpgGreenBright   = BrandGreen
private val RpgGreenDark     = Color(0xFF4F9A1F)
private val RpgAmber         = Color(0xFFFFB020)
private val RpgTextPrimary   = Color(0xFFF2F8E8)
private val RpgTextMuted     = Color(0xFFA8BB8E)
private val RpgTextDim       = Color(0xFF587043)

private val RpgScheme = darkColorScheme(
    primary              = RpgGreenBright,
    onPrimary            = RpgBgDeep,
    primaryContainer     = RpgSurface2,
    onPrimaryContainer   = RpgTextPrimary,

    secondary            = RpgAmber,
    onSecondary          = RpgBgDeep,
    secondaryContainer   = RpgSurface2,
    onSecondaryContainer = RpgTextPrimary,

    tertiary             = RpgGreenDark,
    onTertiary           = RpgBgDeep,
    tertiaryContainer    = RpgSurface2,
    onTertiaryContainer  = RpgTextPrimary,

    background           = RpgBgDeep,
    onBackground         = RpgTextPrimary,

    surface              = RpgSurface1,
    onSurface            = RpgTextPrimary,
    surfaceVariant       = RpgSurface1,
    onSurfaceVariant     = RpgTextMuted,

    outline              = RpgBorderSubtle,
    outlineVariant       = RpgBorderSubtle,

    error                = Color(0xFFFF6B6B),
    onError              = RpgBgDeep,
    errorContainer       = Color(0xFF3D1515),
    onErrorContainer     = Color(0xFFFFB4B4),
)

private val AuroraLightScheme = lightColorScheme(
    primary            = BrandGreen,
    onPrimary          = LightOnBackground,
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

@Immutable
data class TaskExtendedColors(
    val priorityHigh: Color,
    val priorityHighBg: Color,
    val priorityMedium: Color,
    val priorityMediumBg: Color,
    val priorityLow: Color,
    val priorityLowBg: Color,

    val tagWork: Color,
    val tagWorkBg: Color,
    val tagPersonal: Color,
    val tagPersonalBg: Color,
    val tagHealth: Color,
    val tagHealthBg: Color,
    val tagOther: Color,
    val tagOtherBg: Color,

    val success: Color,
    val successBg: Color,
    val warning: Color,
    val warningBg: Color,

    val subText: Color,
    val cardBg: Color,
    val accentColor: Color,

    val taskCardSurface: Color,
    val taskCardBorder: Color,
    val taskCardOverdueSurface: Color,
    val taskCardOverdueBorder: Color,
    val taskCardCompletedSurface: Color,
    val taskCardCompletedBorder: Color,
    val taskMetadataText: Color,
)

val RpgExtendedColors = TaskExtendedColors(
    priorityHigh     = Color(0xFFFF7A6B),
    priorityHighBg   = Color(0xFF2E1410),
    priorityMedium   = RpgAmber,
    priorityMediumBg = Color(0xFF2E2410),
    priorityLow      = RpgGreenBright,
    priorityLowBg    = Color(0xFF18260E),

    tagWork        = Color(0xFF8FB3FF),
    tagWorkBg      = Color(0xFF14203A),
    tagPersonal    = Color(0xFFED93B1),
    tagPersonalBg  = Color(0xFF2E1626),
    tagHealth      = Color(0xFF4DBD94),
    tagHealthBg    = Color(0xFF0F2622),
    tagOther       = RpgTextMuted,
    tagOtherBg     = RpgSurface2,

    success   = RpgGreenBright,
    successBg = Color(0xFF18260E),
    warning   = RpgAmber,
    warningBg = Color(0xFF2E2410),

    subText     = RpgTextMuted,
    cardBg      = RpgSurface1,
    accentColor = RpgAmber,

    taskCardSurface = Color(0xFF101A0C),
    taskCardBorder = RpgBorderSubtle,
    taskCardOverdueSurface = Color(0xFF24130F),
    taskCardOverdueBorder = Color(0xFFFF7A6B).copy(alpha = .62f),
    taskCardCompletedSurface = Color(0xFF13220D),
    taskCardCompletedBorder = BrandGreen.copy(alpha = .46f),
    taskMetadataText = RpgTextMuted,
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

    success   = BrandGreen,
    successBg = Color(0xFFF1FBE4),
    warning   = AuroraAmber,
    warningBg = AuroraAmberLight,

    subText     = Color(0xFF6F7769),
    cardBg      = LightCardBg,
    accentColor = AuroraAmber,

    taskCardSurface = Color.White,
    taskCardBorder = Color(0xFFE2E6DE),
    taskCardOverdueSurface = Color(0xFFFFF4F1),
    taskCardOverdueBorder = Color(0xFFE8553A),
    taskCardCompletedSurface = Color(0xFFFAFFF2),
    taskCardCompletedBorder = BrandGreen,
    taskMetadataText = Color(0xFF6F7769),
)

val LocalExtendedColors = staticCompositionLocalOf { RpgExtendedColors }

@Composable
fun TaskManagementTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) RpgScheme else AuroraLightScheme
    val extendedColors = if (darkTheme) RpgExtendedColors else LightExtendedColors

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

object TaskTheme {
    val colors: TaskExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColors.current
}
