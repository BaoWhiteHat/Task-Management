package com.example.taskmanagement.presentation.focus

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskmanagement.R
import kotlinx.coroutines.delay

private val StoryMono = FontFamily.Monospace

// Parchment palette
private val PaperTop = Color(0xFFF5E7C6)
private val PaperBottom = Color(0xFFE7D2A2)
private val PaperBorder = Color(0xFF8A6D43)
private val InkHeading = Color(0xFF9C5A1C)
private val InkBody = Color(0xFF4A3520)

// Typing speed: higher = slower (ms per character)
private const val TYPE_DELAY_MS = 42L

private data class StoryBeat(
    val heading: String,
    val body: String,
    val sprite: Int
)

private val storyBeats = listOf(
    StoryBeat(
        heading = "THE WITHERING",
        body = "Long ago the World Tree stood tall, and its leaves kept the realm in balance. " +
                "But chaos crept in — unfinished quests, broken focus, forgotten promises — " +
                "and the great tree began to wither.",
        sprite = R.drawable.tree_dead
    ),
    StoryBeat(
        heading = "THE LAST SEEDLING",
        body = "From the dying roots, a single seedling remains. It feeds on one thing only: " +
                "your focus. Every task you face takes the form of a foe that must be defeated.",
        sprite = R.drawable.tree_stage_1
    ),
    StoryBeat(
        heading = "YOUR QUEST",
        body = "Each focus session is a battle. Defeat the foe, and your effort becomes light and " +
                "water for the seedling. Session by session, the World Tree grows once more — " +
                "and so do you.",
        sprite = R.drawable.tree_stage_8
    )
)

@Composable
fun StoryScreen(
    onNavigateBack: () -> Unit = {},
    onBegin: () -> Unit = {}
) {
    var page by remember { mutableIntStateOf(0) }
    val isLast = page == storyBeats.lastIndex

    Box(modifier = Modifier.fillMaxSize().background(BgDeep)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Surface1)
                        .border(0.5.dp, BorderSubtle, CircleShape)
                        .clickable { if (page > 0) page-- else onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.KeyboardArrowLeft, "Back", tint = TextPrimary) }

                Text("THE LEGEND", fontFamily = StoryMono, fontSize = 13.sp, color = GreenBright, letterSpacing = 2.sp)
                Spacer(Modifier.size(40.dp))
            }

            // The book page
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.verticalGradient(listOf(PaperTop, PaperBottom)))
                    .border(2.dp, PaperBorder, RoundedCornerShape(16.dp))
            ) {
                AnimatedContent(
                    targetState = page,
                    transitionSpec = {
                        val forward = targetState > initialState
                        val dir = if (forward) 1 else -1
                        (slideInHorizontally(tween(350)) { w -> dir * w } + fadeIn(tween(350))) togetherWith
                                (slideOutHorizontally(tween(350)) { w -> -dir * w } + fadeOut(tween(350)))
                    },
                    label = "story-page"
                ) { p ->
                    PageContent(beat = storyBeats[p])
                }
            }

            // Page dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                storyBeats.indices.forEach { i ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (i == page) 9.dp else 7.dp)
                            .clip(CircleShape)
                            .background(if (i == page) GreenBright else BorderSubtle)
                    )
                }
            }

            // Next / Begin
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(GreenBright)
                    .clickable { if (isLast) onBegin() else page++ }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isLast) "Begin your quest" else "Next",
                    fontFamily = StoryMono,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = BgDeep,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun PageContent(beat: StoryBeat) {
    var visible by remember { mutableIntStateOf(0) }
    var done by remember { mutableStateOf(false) }

    LaunchedEffect(beat.body) {
        visible = 0
        done = false
        for (i in 1..beat.body.length) {
            visible = i
            delay(TYPE_DELAY_MS)
        }
        done = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    visible = beat.body.length
                    done = true
                }
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(beat.sprite),
                    contentDescription = beat.heading,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.height(170.dp)
                )
            }

            Text(
                text = beat.heading,
                fontFamily = StoryMono,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = InkHeading,
                letterSpacing = 2.sp
            )
            Text(
                text = beat.body.take(visible),
                fontFamily = StoryMono,
                fontSize = 13.sp,
                color = InkBody,
                lineHeight = 22.sp
            )
        }

        // RPG-style "continue" indicator: blinking ... shown once the line finishes
        if (done) {
            val infinite = rememberInfiniteTransition(label = "dots")
            val dotAlpha by infinite.animateFloat(
                initialValue = 1f,
                targetValue = 0.15f,
                animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
                label = "dotAlpha"
            )
            Text(
                text = "...",
                fontFamily = StoryMono,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = InkHeading,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 12.dp)
                    .alpha(dotAlpha)
            )
        }
    }
}