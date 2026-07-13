package com.example.taskmanagement.presentation.appearance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskmanagement.presentation.ui.theme.AppThemeMode
import com.example.taskmanagement.presentation.ui.theme.AppearanceFontStyle
import com.example.taskmanagement.presentation.ui.theme.AppearanceState
import com.example.taskmanagement.presentation.ui.theme.AppearanceTextSize
import com.example.taskmanagement.presentation.ui.theme.TaskTheme

@Composable
fun AppearanceScreen(
    state: AppearanceState,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onTextSizeChange: (AppearanceTextSize) -> Unit,
    onFontStyleChange: (AppearanceFontStyle) -> Unit,
    onNavigateBack: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(scheme.surface)
                        .border(0.5.dp, scheme.outline, CircleShape)
                        .clickable(onClick = onNavigateBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Back", tint = scheme.onSurface)
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    "Appearance",
                    style = MaterialTheme.typography.titleLarge,
                    color = scheme.onBackground
                )
            }

            AppearanceSection(title = "Theme") {
                ThemeSwitchRow(
                    selectedMode = state.themeMode,
                    onSelected = onThemeModeChange
                )
            }

            AppearanceSection(title = "Text size") {
                SegmentedSelector(
                    options = AppearanceTextSize.entries,
                    selected = state.textSize,
                    label = { it.label },
                    onSelected = onTextSizeChange
                )
            }

            AppearanceSection(title = "Font style") {
                SegmentedSelector(
                    options = AppearanceFontStyle.entries,
                    selected = state.fontStyle,
                    label = { it.label },
                    onSelected = onFontStyleChange
                )
            }

            AppearanceSection(title = "Preview") {
                PreviewTaskCard()
            }
        }
    }
}

@Composable
private fun AppearanceSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val appearanceColors = TaskTheme.appearanceControls
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = appearanceColors.sectionLabel
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content
        )
    }
}

@Composable
private fun ThemeSwitchRow(
    selectedMode: AppThemeMode,
    onSelected: (AppThemeMode) -> Unit
) {
    val appearanceColors = TaskTheme.appearanceControls
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(appearanceColors.controlContainer)
            .border(0.5.dp, appearanceColors.controlOutline, RoundedCornerShape(14.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ThemeOption(
            mode = AppThemeMode.LIGHT,
            selected = selectedMode == AppThemeMode.LIGHT,
            onClick = { onSelected(AppThemeMode.LIGHT) },
            modifier = Modifier.weight(1f)
        )
        ThemeOption(
            mode = AppThemeMode.DARK,
            selected = selectedMode == AppThemeMode.DARK,
            onClick = { onSelected(AppThemeMode.DARK) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ThemeOption(
    mode: AppThemeMode,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appearanceColors = TaskTheme.appearanceControls
    val background = if (selected) {
        appearanceColors.selectedContainer
    } else {
        appearanceColors.unselectedContainer
    }
    val border = if (selected) appearanceColors.selectedBorder else Color.Transparent
    val content = if (selected) {
        appearanceColors.selectedContent
    } else {
        appearanceColors.unselectedContent
    }
    val icon = if (mode == AppThemeMode.LIGHT) Icons.Default.LightMode else Icons.Default.DarkMode

    Row(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(background)
            .border(
                width = if (selected) 1.dp else 0.dp,
                color = border,
                shape = RoundedCornerShape(11.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = mode.label, tint = content, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            mode.label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = content
        )
    }
}

@Composable
private fun <T> SegmentedSelector(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelected: (T) -> Unit
) {
    val appearanceColors = TaskTheme.appearanceControls
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(appearanceColors.controlContainer)
            .border(0.5.dp, appearanceColors.controlOutline, RoundedCornerShape(14.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            val background = if (isSelected) {
                appearanceColors.selectedContainer
            } else {
                appearanceColors.unselectedContainer
            }
            val border = if (isSelected) appearanceColors.selectedBorder else Color.Transparent
            val content = if (isSelected) {
                appearanceColors.selectedContent
            } else {
                appearanceColors.unselectedContent
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(background)
                    .border(
                        width = if (isSelected) 1.dp else 0.dp,
                        color = border,
                        shape = RoundedCornerShape(11.dp)
                    )
                    .clickable { onSelected(option) }
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label(option),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = content,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun PreviewTaskCard() {
    val scheme = MaterialTheme.colorScheme
    val colors = TaskTheme.colors
    val surface = colors.taskCardSurface.copy(alpha = .96f)
    val shape = RoundedCornerShape(13.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(surface)
            .border(1.dp, colors.taskCardBorder, shape)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 34.dp, height = 38.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(colors.priorityHigh.copy(alpha = .10f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .border(2.dp, colors.priorityHigh, CircleShape)
            )
        }

        Spacer(Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Complete Mobile Assignment",
                    style = MaterialTheme.typography.titleSmall.copy(letterSpacing = 0.sp),
                    fontWeight = FontWeight.SemiBold,
                    color = scheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "HIGH",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.priorityHigh,
                    maxLines = 1,
                    modifier = Modifier
                        .clip(RoundedCornerShape(7.dp))
                        .background(colors.priorityHighBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Text(
                "Today \u00B7 8:00 PM \u00B7 45 min",
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.sp),
                fontWeight = FontWeight.Medium,
                color = colors.taskMetadataText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
