package com.novah.mathmind.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.novah.mathmind.ui.theme.NeuColors
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class ReactionState {
    WAITING, READY, TOO_EARLY, RESULT
}

/**
 * Reaction Time game with difficulty levels and neubrutalism design.
 * Difficulty affects the random delay range:
 * - Easy: 3000-6000ms (longer wait, more relaxed)
 * - Medium: 1500-5000ms (standard)
 * - Hard: 500-2000ms (short wait, requires sharp focus)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReactionTimeScreen(navController: NavHostController, difficulty: String = "medium") {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("reaction_time_prefs", Context.MODE_PRIVATE)
    }

    var state by remember { mutableStateOf(ReactionState.WAITING) }
    var reactionTimeMs by remember { mutableLongStateOf(0L) }
    var greenShownAt by remember { mutableLongStateOf(0L) }
    var bestTime by remember { mutableLongStateOf(prefs.getLong("best_reaction_time_$difficulty", 0L)) }

    // Delay range based on difficulty
    val delayRange = when (difficulty) {
        "easy" -> 3000L to 6000L
        "medium" -> 1500L to 5000L
        "hard" -> 500L to 2000L
        else -> 1500L to 5000L
    }

    LaunchedEffect(key1 = state) {
        if (state == ReactionState.WAITING) {
            val randomDelay = Random.nextLong(delayRange.first, delayRange.second + 1)
            delay(randomDelay)
            if (state == ReactionState.WAITING) {
                greenShownAt = System.currentTimeMillis()
                state = ReactionState.READY
            }
        }
    }

    fun saveBestTime(timeMs: Long) {
        if (bestTime == 0L || timeMs < bestTime) {
            bestTime = timeMs
            prefs.edit().putLong("best_reaction_time_$difficulty", timeMs).apply()
        }
    }

    // Neubrutalism colors for states
    val backgroundColor = when (state) {
        ReactionState.WAITING -> Color(0xFFFF6B6B)
        ReactionState.READY -> NeuColors.Green
        ReactionState.TOO_EARLY -> NeuColors.Orange
        ReactionState.RESULT -> NeuColors.Blue
    }

    val displayText = when (state) {
        ReactionState.WAITING -> "Wait for Green..."
        ReactionState.READY -> "TAP NOW!"
        ReactionState.TOO_EARLY -> "Too early!\nTap to try again"
        ReactionState.RESULT -> "${reactionTimeMs} ms\nTap to try again"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reaction Time - ${difficulty.replaceFirstChar { it.uppercase() }}",
                        fontWeight = FontWeight.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NeuColors.Yellow,
                    titleContentColor = NeuColors.Black,
                    navigationIconContentColor = NeuColors.Black
                ),
                modifier = Modifier.border(width = 3.dp, color = NeuColors.Black)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor)
                .border(4.dp, NeuColors.Black)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    when (state) {
                        ReactionState.WAITING -> state = ReactionState.TOO_EARLY
                        ReactionState.READY -> {
                            reactionTimeMs = System.currentTimeMillis() - greenShownAt
                            saveBestTime(reactionTimeMs)
                            state = ReactionState.RESULT
                        }
                        ReactionState.TOO_EARLY, ReactionState.RESULT -> {
                            state = ReactionState.WAITING
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val textShape = RoundedCornerShape(16.dp)
                Box(
                    modifier = Modifier
                        .padding(24.dp)
                        .clip(textShape)
                        .background(NeuColors.Black.copy(alpha = 0.2f))
                        .border(3.dp, NeuColors.Black, textShape)
                        .padding(32.dp)
                ) {
                    Text(
                        text = displayText,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = NeuColors.Black,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                if (bestTime > 0L) {
                    val bestShape = RoundedCornerShape(8.dp)
                    Box(
                        modifier = Modifier
                            .clip(bestShape)
                            .background(NeuColors.Yellow)
                            .border(2.dp, NeuColors.Black, bestShape)
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "🏆 Best: ${bestTime} ms",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeuColors.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
