package com.example.taskmanagement.presentation.focus

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.taskmanagement.presentation.ui.theme.LightRpgPrimaryCta
import com.example.taskmanagement.presentation.ui.theme.LightRpgSelectedBorder
import com.example.taskmanagement.presentation.ui.theme.RpgLimeGreen

internal val BgDeep        = Color(0xFF081105)   // nền xanh đen dịu hơn
internal val Surface1      = Color(0xFF101C0C)   // card nền
internal val Surface2      = Color(0xFF162610)   // card active
internal val BorderSubtle  = Color(0xFF2D4A1E)   // border rõ hơn

internal val GreenBright   = RpgLimeGreen        // xanh sáng tươi
internal val GreenMid      = Color(0xFF86D72F)   // xanh trung gian
internal val GreenDark     = Color(0xFF4F9A1F)   // xanh đậm
internal val AmberAccent   = Color(0xFFFFB020)   // break màu vàng tươi hơn

internal val TextPrimary   = Color(0xFFF2F8E8)
internal val TextMuted     = Color(0xFFA8BB8E)
internal val TextDim       = Color(0xFF587043)

internal val TrunkBrown    = Color(0xFF7A5625)

// ---- Battle-only colors ----
internal val HpRed         = Color(0xFFE5484D)   // enemy HP / boss / flee
internal val HpTrack       = Color(0xFF2A1418)   // HP bar track

// ---- Focus Quest battle palette (two modes) -------------------------------
// Default (no background): the original dark theme, untouched.
// Background equipped: white glass panels + dark ink text, readable on any scene.
@Immutable
internal data class BattlePalette(
    val panel: Color,
    val border: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val textDim: Color,
    val green: Color,
    val amber: Color,
    val hp: Color,
    val pill: Color,
    val isLight: Boolean
)

internal val DarkPalette = BattlePalette(
    panel = Surface1,
    border = BorderSubtle,
    textPrimary = TextPrimary,
    textMuted = TextMuted,
    textDim = TextDim,
    green = GreenBright,
    amber = AmberAccent,
    hp = HpRed,
    pill = Surface2,
    isLight = false
)

// Light mode = white glass + dark ink. Text colors are kept DARK and strong so
// they stay readable even when the panel is fairly see-through over a busy photo.
// `panel` alpha is the transparency knob: lower = more see-through.
// `pill` is a more opaque box used for the small ambient-sound chip so it stays solid.
internal val LightPalette = BattlePalette(
    panel = Color.White.copy(alpha = 0.70f),
    border = LightRpgSelectedBorder.copy(alpha = 0.60f),
    textPrimary = Color(0xFF11200A),
    textMuted = Color(0xFF2E3826),
    textDim = Color(0xFF45503C),
    green = LightRpgPrimaryCta,
    amber = Color(0xFF8A5A00),
    hp = Color(0xFFB02C20),
    pill = Color(0xF2FFFFFF),
    isLight = true
)
