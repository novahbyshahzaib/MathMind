package com.novah.mathmind.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Possible states of the Reaction Time game.
 */
enum class ReactionState {
    WAITING,     // Red screen - waiting for green
    READY,       // Green screen - tap now!
    TOO_EARLY,   // User tapped during red
    RESULT       // Showing measured reaction time
}

/**
 * Reaction Time game screen.
 * Tests the user's reflexes by measuring the time between a visual signal
 * (screen turning green) and the user's tap. Saves best time to SharedPreferences.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReactionTimeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("reaction_time_prefs", Context.MODE_PRIVATE)
    }

    var state by remember { mutableStateOf(ReactionState.WAITING) }
    var reactionTimeMs by remember { mutableLongStateOf(0L) }
    var greenShownAt by remember { mutableLongStateOf(0L) }
    var bestTime by remember { mutableLongStateOf(prefs.getLong("best_reaction_time", 0L)) }

    // Coroutine that handles the random delay before turning green
    LaunchedEffect(key1 = state) {
        if (state == ReactionState.WAITING) {
            // Random delay between 1500ms and 5000ms
            val randomDelay = Random.nextLong(1500L, 5001L)
            delay(randomDelay)
            // Only transition to READY if still in WAITING state
            // (user might have tapped too early)
            if (state == ReactionState.WAITING) {
                greenShownAt = System.currentTimeMillis()
                state = ReactionState.READY
            }
        }
    }

    /** Saves the new best reaction time to SharedPreferences */
    fun saveBestTime(timeMs: Long) {
        if (bestTime == 0L || timeMs < bestTime) {
            bestTime = timeMs
            prefs.edit().putLong("best_reaction_time", timeMs).apply()
        }
    }

    // Determine screen colors and text based on current state
    val backgroundColor = when (state) {
        ReactionState.WAITING -> Color(0xFFD32F2F)   // Red
        ReactionState.READY -> Color(0xFF388E3C)      // Green
        ReactionState.TOO_EARLY -> Color(0xFFFF6F00)   // Orange
        ReactionState.RESULT -> Color(0xFF1565C0)      // Blue
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
                title = { Text("Reaction Time") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    when (state) {
                        ReactionState.WAITING -> {
                            // Tapped too early while still red
                            state = ReactionState.TOO_EARLY
                        }
                        ReactionState.READY -> {
                            // Measure reaction time
                            reactionTimeMs = System.currentTimeMillis() - greenShownAt
                            saveBestTime(reactionTimeMs)
                            state = ReactionState.RESULT
                        }
                        ReactionState.TOO_EARLY, ReactionState.RESULT -> {
                            // Restart the game
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
                Text(
                    text = displayText,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                // Display best time if one exists
                if (bestTime > 0L) {
                    Text(
                        text = "Best: ${bestTime} ms",
                        fontSize = 20.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
