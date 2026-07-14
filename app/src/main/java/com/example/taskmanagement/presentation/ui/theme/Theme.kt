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
import androidx.compose.ui.text.font.FontFamily

private val RpgBgDeep       = Color(0xFF081105)
private val RpgSurface1      = Color(0xFF101C0C)
private val RpgSurface2      = Color(0xFF162610)
private val RpgBorderSubtle  = Color(0xFF2D4A1E)
private val RpgGreenBright   = RpgLimeGreen
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
    primary            = LightRpgPrimaryCta,
    onPrimary          = LightRpgOnPrimary,
    primaryContainer   = LightRpgSelectedBackground,
    onPrimaryContainer = LightRpgPressedCta,
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
    val successText: Color,
    val successBadgeBackground: Color,
    val successBadgeText: Color,
    val completedMetadataText: Color,
    val warning: Color,
    val warningBg: Color,
    val overdueAccent: Color,
    val overdueBg: Color,

    val subText: Color,
    val cardBg: Color,
    val accentColor: Color,
    val rpgPrimaryCta: Color,
    val rpgPressedCta: Color,
    val rpgSelectedBorder: Color,
    val rpgHighlight: Color,
    val rpgSelectedBackground: Color,
    val rpgOnPrimaryCta: Color,

    val taskCardSurface: Color,
    val taskCardBorder: Color,
    val taskCardOverdueSurface: Color,
    val taskCardOverdueBorder: Color,
    val taskCardCompletedSurface: Color,
    val taskCardCompletedBorder: Color,
    val taskMetadataText: Color,

    val selectedPriorityHighText: Color,
    val selectedPriorityMediumText: Color,
    val selectedPriorityLowText: Color,
    val selectedTagWorkText: Color,
    val selectedTagPersonalText: Color,
    val selectedTagHealthText: Color,
)

val RpgExtendedColors = TaskExtendedColors(
    priorityHigh     = Color(0xFFFF7B6B),
    priorityHighBg   = Color(0xFF2E1410),
    priorityMedium   = RpgAmber,
    priorityMediumBg = Color(0xFF2E2410),
    priorityLow      = Color(0xFFB7F34A),
    priorityLowBg    = Color(0xFF18260E),

    tagWork        = Color(0xFF8FB3FF),
    tagWorkBg      = Color(0xFF14203A),
    tagPersonal    = Color(0xFFD7B8FF),
    tagPersonalBg  = Color(0xFF291A3D),
    tagHealth      = Color(0xFF4DBD94),
    tagHealthBg    = Color(0xFF0F2622),
    tagOther       = RpgTextMuted,
    tagOtherBg     = RpgSurface2,

    success   = Color(0xFF86D72F),
    successBg = Color(0xFF18260E),
    successText = Color(0xFF86D72F),
    successBadgeBackground = Color(0xFF86D72F).copy(alpha = .14f),
    successBadgeText = Color(0xFF86D72F),
    completedMetadataText = Color(0xFF86D72F),
    warning   = RpgAmber,
    warningBg = Color(0xFF2E2410),
    overdueAccent = Color(0xFFFF6B5E),
    overdueBg = Color(0xFFFF6B5E).copy(alpha = .16f),

    subText     = RpgTextMuted,
    cardBg      = RpgSurface1,
    accentColor = RpgAmber,
    rpgPrimaryCta = RpgGreenBright,
    rpgPressedCta = RpgGreenDark,
    rpgSelectedBorder = RpgGreenBright,
    rpgHighlight = RpgGreenBright,
    rpgSelectedBackground = RpgSurface2,
    rpgOnPrimaryCta = RpgBgDeep,

    taskCardSurface = Color(0xFF101A0C),
    taskCardBorder = RpgBorderSubtle,
    taskCardOverdueSurface = Color(0xFF24130F),
    taskCardOverdueBorder = Color(0xFFFF6B5E).copy(alpha = .62f),
    taskCardCompletedSurface = Color(0xFF13220D),
    taskCardCompletedBorder = Color(0xFF86D72F).copy(alpha = .46f),
    taskMetadataText = RpgTextMuted,

    selectedPriorityHighText = RpgBgDeep,
    selectedPriorityMediumText = RpgBgDeep,
    selectedPriorityLowText = RpgBgDeep,
    selectedTagWorkText = RpgBgDeep,
    selectedTagPersonalText = RpgBgDeep,
    selectedTagHealthText = RpgBgDeep,
)

val LightExtendedColors = TaskExtendedColors(
    priorityHigh     = Color(0xFFD94A3D),
    priorityHighBg   = PriorityHighBg,
    priorityMedium   = Color(0xFFA66A00),
    priorityMediumBg = PriorityMediumBg,
    priorityLow      = Color(0xFF4F7F1A),
    priorityLowBg    = PriorityLowBg,

    tagWork        = TagWork,
    tagWorkBg      = TagWorkBg,
    tagPersonal    = Color(0xFF7B3FB2),
    tagPersonalBg  = Color(0xFFF2E7FF),
    tagHealth      = TagHealth,
    tagHealthBg    = TagHealthBg,
    tagOther       = TagOther,
    tagOtherBg     = TagOtherBg,

    success   = Color(0xFF2E7D32),
    successBg = Color(0xFFF1FBE4),
    successText = Color(0xFF2E7D32),
    successBadgeBackground = Color(0xFFF1FBE4),
    successBadgeText = Color(0xFF2E7D32),
    completedMetadataText = Color(0xFF2E7D32),
    warning   = AuroraAmber,
    warningBg = AuroraAmberLight,
    overdueAccent = Color(0xFFB3261E),
    overdueBg = Color(0xFFFFEDEA),

    subText     = Color(0xFF6F7769),
    cardBg      = LightCardBg,
    accentColor = AuroraAmber,
    rpgPrimaryCta = LightRpgPrimaryCta,
    rpgPressedCta = LightRpgPressedCta,
    rpgSelectedBorder = LightRpgSelectedBorder,
    rpgHighlight = LightRpgHighlight,
    rpgSelectedBackground = LightRpgSelectedBackground,
    rpgOnPrimaryCta = LightRpgOnPrimary,

    taskCardSurface = Color.White,
    taskCardBorder = Color(0xFFE2E6DE),
    taskCardOverdueSurface = Color(0xFFFFF4F1),
    taskCardOverdueBorder = Color(0xFFB3261E),
    taskCardCompletedSurface = Color(0xFFFAFFF2),
    taskCardCompletedBorder = Color(0xFF2E7D32),
    taskMetadataText = Color(0xFF6F7769),

    selectedPriorityHighText = Color.White,
    selectedPriorityMediumText = Color.White,
    selectedPriorityLowText = Color.White,
    selectedTagWorkText = Color.White,
    selectedTagPersonalText = Color.White,
    selectedTagHealthText = Color.White,
)

val LocalExtendedColors = staticCompositionLocalOf { RpgExtendedColors }
val LocalAppFontFamily = staticCompositionLocalOf<FontFamily> { Pixel }
val LocalAppearanceControlColors = staticCompositionLocalOf { DarkAppearanceControlColors }

@Immutable
data class AppearanceControlColors(
    val selectedContainer: Color,
    val selectedContent: Color,
    val selectedBorder: Color,
    val unselectedContainer: Color,
    val unselectedContent: Color,
    val controlContainer: Color,
    val controlOutline: Color,
    val sectionLabel: Color,
)

val LightAppearanceControlColors = AppearanceControlColors(
    selectedContainer = LightRpgSelectedBackground,
    selectedContent = LightRpgPrimaryCta,
    selectedBorder = LightRpgSelectedBorder,
    unselectedContainer = Color.Transparent,
    unselectedContent = Color(0xFF477565),
    controlContainer = Color(0xFFEDF5F1),
    controlOutline = LightOutline,
    sectionLabel = LightOnSurfaceVar,
)

val DarkAppearanceControlColors = AppearanceControlColors(
    selectedContainer = Color(0xFF29481E),
    selectedContent = Color(0xFFDDFC9C),
    selectedBorder = RpgLimeGreen,
    unselectedContainer = Color.Transparent,
    unselectedContent = Color(0xFFA5B9AC),
    controlContainer = Color(0xFF17221A),
    controlOutline = RpgBorderSubtle,
    sectionLabel = RpgTextMuted,
)

data class AppearanceState(
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val textSize: AppearanceTextSize = AppearanceTextSize.DEFAULT,
    val fontStyle: AppearanceFontStyle = AppearanceFontStyle.PIXEL
)

enum class AppThemeMode(
    val storedValue: String,
    val label: String
) {
    LIGHT("light", "Light"),
    DARK("dark", "Dark");

    fun resolveDarkTheme(): Boolean =
        when (this) {
            LIGHT -> false
            DARK -> true
        }

    companion object {
        fun fromStoredValue(value: String?): AppThemeMode =
            entries.firstOrNull { it.storedValue == value } ?: DARK
    }
}

enum class AppearanceTextSize(
    val storedValue: String,
    val label: String,
    val summaryLabel: String,
    val scale: Float
) {
    SMALL("small", "Small", "Small text", 0.90f),
    DEFAULT("default", "Default", "Default text", 1.00f),
    LARGE("large", "Large", "Large text", 1.15f);

    companion object {
        fun fromStoredValue(value: String?): AppearanceTextSize =
            entries.firstOrNull { it.storedValue == value } ?: DEFAULT
    }
}

enum class AppearanceFontStyle(
    val storedValue: String,
    val label: String
) {
    PIXEL("pixel", "Pixel"),
    CLEAN("clean", "Clean"),
    ROUNDED("rounded", "Rounded");

    companion object {
        fun fromStoredValue(value: String?): AppearanceFontStyle =
            entries.firstOrNull { it.storedValue == value } ?: PIXEL
    }
}

fun AppearanceState.summary(): String =
    "${themeMode.label} \u00B7 ${textSize.summaryLabel} \u00B7 ${fontStyle.label}"

@Composable
fun TaskManagementTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    textSize: AppearanceTextSize = AppearanceTextSize.DEFAULT,
    fontStyle: AppearanceFontStyle = AppearanceFontStyle.PIXEL,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) RpgScheme else AuroraLightScheme
    val extendedColors = if (darkTheme) RpgExtendedColors else LightExtendedColors
    val appearanceControlColors =
        if (darkTheme) DarkAppearanceControlColors else LightAppearanceControlColors
    val fontFamily = appFontFamily(fontStyle)

    androidx.compose.runtime.CompositionLocalProvider(
        LocalExtendedColors provides extendedColors,
        LocalAppearanceControlColors provides appearanceControlColors,
        LocalAppFontFamily provides fontFamily
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = appTypography(textSize, fontStyle),
            content = content
        )
    }
}

object TaskTheme {
    val colors: TaskExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColors.current

    val fontFamily: FontFamily
        @Composable
        @ReadOnlyComposable
        get() = LocalAppFontFamily.current

    val appearanceControls: AppearanceControlColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppearanceControlColors.current
}
