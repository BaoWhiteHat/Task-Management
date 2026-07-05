package com.example.taskmanagement.presentation.focus

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskmanagement.R

enum class StoryMode {
    LORE,
    NOTEBOOK
}

private val BookMono = FontFamily.Monospace
private val PaperTop = Color(0xFFF2E2BC)
private val PaperBottom = Color(0xFFD9BF86)
private val PaperBorder = Color(0xFF9A7135)
private val InkHeading = Color(0xFF5B3515)
private val InkBody = Color(0xFF392A1A)
private val AshBlack = Color(0xFF020503)
private val ChronicleGold = Color(0xFFD9A441)

private data class BookPage(
    val era: String,
    val title: String,
    val body: String,
    val illustration: Int
)

private val lorePages = listOf(
    BookPage(
        era = "I. The Heartwood",
        title = "The Heartwood",
        body = "Long ago, the Tree of Knowledge stood at the heart of the world.\n\n" +
            "It was nourished not by water, but by discipline, focus, and human will.\n\n" +
            "Each act of perseverance made its branches shine brighter.",
        illustration = R.drawable.tree_stage_8
    ),
    BookPage(
        era = "II. The Withering",
        title = "The Withering",
        body = "But discipline faded, and restless minds turned away.\n\n" +
            "The sacred light weakened, and the Heartwood began to wither.\n\n" +
            "Purpose vanished into a long age of wandering and ruin.",
        illustration = R.drawable.tree_dead
    ),
    BookPage(
        era = "III. The Scattered Tomes",
        title = "The Scattered Tomes",
        body = "On the night the ancient tree broke, its wisdom shattered into scattered tomes.\n\n" +
            "Each relic carried a spark of lost power, waiting to be recovered.\n\n" +
            "They are the final fragments of a forgotten age.",
        illustration = R.drawable.tome_43
    ),
    BookPage(
        era = "IV. The Last Seedling",
        title = "The Last Seedling",
        body = "Beneath the ashes of the old roots, one final seedling endured.\n\n" +
            "Small, fragile, and alone, it became the last hope of a dying world.\n\n" +
            "Its light is faint, but not yet gone.",
        illustration = R.drawable.tree_stage_1
    ),
    BookPage(
        era = "V. Your Quest Begins",
        title = "Your Quest Begins",
        body = "Now the burden passes to you.\n\n" +
            "Every focused battle, every completed quest, and every recovered tome will help the seedling grow again.\n\n" +
            "Restore what was lost before the ashes consume everything.",
        illustration = R.drawable.tree_stage_6
    )
)

private val notebookPages = listOf(
    BookPage(
        era = "FIELD NOTE I",
        title = "Welcome, Traveler",
        body = "Focus Quest turns your study routine into an RPG journey.\n\n" +
            "Your tasks become quests, and your focus sessions become battles that help restore the Tree of Knowledge.",
        illustration = R.drawable.tree_stage_6
    ),
    BookPage(
        era = "FIELD NOTE II",
        title = "Create Quests",
        body = "Add study tasks with priorities, deadlines, and tags.\n\n" +
            "Use your quest list to decide what needs your attention first.",
        illustration = R.drawable.tome_14
    ),
    BookPage(
        era = "FIELD NOTE III",
        title = "Enter Focus Battle",
        body = "Choose a quest, prepare your loadout, and begin a focus session.\n\n" +
            "You can select an ambient sound, arm a tome, and bring guardian items into battle.",
        illustration = R.drawable.tree_stage_3
    ),
    BookPage(
        era = "FIELD NOTE IV",
        title = "Earn Rewards",
        body = "Complete focus battles to earn XP, coins, loot, and streak progress.\n\n" +
            "Your effort helps the Tree of Knowledge grow stronger over time.",
        illustration = R.drawable.ach_11
    ),
    BookPage(
        era = "FIELD NOTE V",
        title = "Use the Shop",
        body = "Spend coins on tomes, sounds, backgrounds, and guardian items.\n\n" +
            "Each item supports your journey in a different way.",
        illustration = R.drawable.ach_20
    ),
    BookPage(
        era = "FIELD NOTE VI",
        title = "Guardian Items",
        body = "Guardian items protect your progress during difficult moments.\n\n" +
            "Streak Shields protect your streak. Focus Potions block early retreat penalties. Tome Seals preserve armed tomes. Loot Magnets add bonus treasure after victory.",
        illustration = R.drawable.ach_30
    ),
    BookPage(
        era = "FIELD NOTE VII",
        title = "Overdue Quests",
        body = "Overdue tasks remain on your quest list and demand attention.\n\n" +
            "Replan them, adjust their deadline, or return with a focused session before they weigh down your journey.",
        illustration = R.drawable.tree_dead
    )
)

@Composable
fun StoryScreen(
    mode: StoryMode,
    onNavigateBack: () -> Unit = {},
    onBegin: () -> Unit = {}
) {
    val pages = if (mode == StoryMode.LORE) lorePages else notebookPages
    val screenTitle = if (mode == StoryMode.LORE) "The Ashes of Mind" else "Adventurer's Notebook"
    val screenSubtitle = if (mode == StoryMode.LORE) {
        "A chronicle of the Tree of Knowledge"
    } else {
        "Quests, battles, rewards, and survival"
    }
    val finalAction = if (mode == StoryMode.LORE) "Begin Quest" else "Start Exploring"
    var page by rememberSaveable(mode) { mutableIntStateOf(0) }
    val isLast = page == pages.lastIndex

    BackHandler {
        if (page > 0) page-- else onNavigateBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF101A0D), BgDeep, AshBlack)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Surface1)
                        .border(0.5.dp, ChronicleGold.copy(alpha = 0.6f), CircleShape)
                        .clickable { if (page > 0) page-- else onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("\u2190", fontFamily = BookMono, fontSize = 18.sp, color = TextPrimary)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        screenTitle,
                        fontFamily = BookMono,
                        fontSize = if (mode == StoryMode.LORE) 14.sp else 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChronicleGold,
                        letterSpacing = 1.5.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        screenSubtitle,
                        fontFamily = BookMono,
                        fontSize = 9.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.size(40.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.verticalGradient(listOf(PaperTop, PaperBottom)))
                    .border(2.dp, PaperBorder, RoundedCornerShape(18.dp))
                    .padding(2.dp)
            ) {
                AnimatedContent(
                    targetState = page,
                    transitionSpec = {
                        val direction = if (targetState > initialState) 1 else -1
                        (slideInHorizontally(tween(320)) { direction * it } + fadeIn(tween(240))) togetherWith
                            (slideOutHorizontally(tween(320)) { -direction * it } + fadeOut(tween(240)))
                    },
                    label = "book-page"
                ) { index ->
                    BookPageContent(pages[index])
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "PAGE ${page + 1} / ${pages.size}",
                    fontFamily = BookMono,
                    fontSize = 10.sp,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    pages.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == page) 9.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == page) ChronicleGold
                                    else BorderSubtle
                                )
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(13.dp))
                        .background(Surface1)
                        .border(0.5.dp, BorderSubtle, RoundedCornerShape(13.dp))
                        .clickable { if (page > 0) page-- else onNavigateBack() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (page > 0) "Back" else "Close",
                        fontFamily = BookMono,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1.5f)
                        .clip(RoundedCornerShape(13.dp))
                        .background(if (isLast) ChronicleGold else GreenBright)
                        .clickable { if (isLast) onBegin() else page++ }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isLast) finalAction else "Continue",
                        fontFamily = BookMono,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BgDeep
                    )
                }
            }
        }
    }
}

@Composable
private fun BookPageContent(page: BookPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(Modifier.size(7.dp).clip(CircleShape).background(InkHeading))
            Text(
                page.era.uppercase(),
                fontFamily = BookMono,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = InkHeading,
                letterSpacing = 1.4.sp
            )
            Box(Modifier.size(7.dp).clip(CircleShape).background(InkHeading))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.radialGradient(
                        listOf(Color(0x66E9B957), Color(0x220D160B), Color(0x000D160B))
                    )
                )
                .border(0.5.dp, PaperBorder.copy(alpha = 0.55f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(page.illustration),
                contentDescription = page.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(164.dp)
            )
        }

        Text(
            page.title,
            fontFamily = BookMono,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = InkHeading,
            textAlign = TextAlign.Center
        )
        Text(
            "\u2726  \u2500\u2500\u2500  \u2726",
            fontFamily = BookMono,
            fontSize = 12.sp,
            color = PaperBorder
        )
        Text(
            page.body,
            fontFamily = BookMono,
            fontSize = 12.sp,
            lineHeight = 19.sp,
            color = InkBody,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
