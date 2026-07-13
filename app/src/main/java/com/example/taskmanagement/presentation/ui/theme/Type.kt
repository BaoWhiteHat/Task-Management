package com.example.taskmanagement.presentation.ui.theme

import android.graphics.Typeface
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

val Pixel = FontFamily.Monospace
private val Clean = FontFamily.SansSerif
private val Rounded = FontFamily(Typeface.create("sans-serif-rounded", Typeface.NORMAL))

fun appFontFamily(fontStyle: AppearanceFontStyle): FontFamily =
    when (fontStyle) {
        AppearanceFontStyle.PIXEL -> Pixel
        AppearanceFontStyle.CLEAN -> Clean
        AppearanceFontStyle.ROUNDED -> Rounded
    }

fun appTypography(
    textSize: AppearanceTextSize = AppearanceTextSize.DEFAULT,
    fontStyle: AppearanceFontStyle = AppearanceFontStyle.PIXEL
): Typography {
    val family = appFontFamily(fontStyle)
    val scale = textSize.scale
    return BaseTypography.scaled(family, scale)
}

private fun Typography.scaled(fontFamily: FontFamily, scale: Float): Typography = Typography(
    displayLarge = displayLarge.scaled(fontFamily, scale),
    displayMedium = displayMedium.scaled(fontFamily, scale),
    displaySmall = displaySmall.scaled(fontFamily, scale),
    headlineLarge = headlineLarge.scaled(fontFamily, scale),
    headlineMedium = headlineMedium.scaled(fontFamily, scale),
    headlineSmall = headlineSmall.scaled(fontFamily, scale),
    titleLarge = titleLarge.scaled(fontFamily, scale),
    titleMedium = titleMedium.scaled(fontFamily, scale),
    titleSmall = titleSmall.scaled(fontFamily, scale),
    bodyLarge = bodyLarge.scaled(fontFamily, scale),
    bodyMedium = bodyMedium.scaled(fontFamily, scale),
    bodySmall = bodySmall.scaled(fontFamily, scale),
    labelLarge = labelLarge.scaled(fontFamily, scale),
    labelMedium = labelMedium.scaled(fontFamily, scale),
    labelSmall = labelSmall.scaled(fontFamily, scale)
)

private fun TextStyle.scaled(fontFamily: FontFamily, scale: Float): TextStyle =
    copy(
        fontFamily = fontFamily,
        fontSize = fontSize.scaled(scale),
        lineHeight = lineHeight.scaled(scale)
    )

private fun TextUnit.scaled(scale: Float): TextUnit =
    if (this == TextUnit.Unspecified) this else (value * scale).sp

private val BaseTypography = Typography(

    displayLarge = TextStyle(
        fontFamily = Pixel,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 48.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = Pixel,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = Pixel,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    headlineLarge = TextStyle(
        fontFamily = Pixel,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Pixel,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Pixel,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),

    titleLarge = TextStyle(
        fontFamily = Pixel,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Pixel,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Pixel,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = Pixel,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.25.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Pixel,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Pixel,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
    ),

    labelLarge = TextStyle(
        fontFamily = Pixel,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Pixel,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Pixel,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.4.sp
    ),
)

val Typography = appTypography()
