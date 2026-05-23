package com.example.taskmanagement.presentation.focus.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val GreenBright = Color(0xFFA8D840)
private val GreenMid    = Color(0xFF6AAD28)
private val GreenDark   = Color(0xFF3D6615)
private val TrunkBrown  = Color(0xFF5A3E1A)

@Composable
fun GrowingTree(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val safe = progress.coerceIn(0f, 1f)

    val canopySize by animateFloatAsState(
        targetValue = 38f + safe * 52f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 60f),
        label = "canopy-size"
    )
    val trunkHeight by animateFloatAsState(
        targetValue = 20f + safe * 28f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 80f),
        label = "trunk-height"
    )
    val canopyColor by animateColorAsState(
        targetValue = when {
            safe < 0.25f -> GreenDark
            safe < 0.6f  -> GreenMid
            else         -> GreenBright
        },
        animationSpec = tween(700),
        label = "canopy-color"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Canopy
        Box(
            modifier = Modifier
                .size(canopySize.dp)
                .background(canopyColor, CircleShape)
        )

        Spacer(Modifier.height(4.dp))

        // Trunk
        Box(
            modifier = Modifier
                .width(10.dp)
                .height(trunkHeight.dp)
                .background(TrunkBrown, RoundedCornerShape(50))
        )

        Spacer(Modifier.height(4.dp))

        // Ground line
        Box(
            modifier = Modifier
                .width(52.dp)
                .height(3.dp)
                .background(GreenDark, RoundedCornerShape(10.dp))
        )
    }
}