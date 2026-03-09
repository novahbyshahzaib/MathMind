package com.novah.mathmind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.novah.mathmind.ui.navigation.Routes
import com.novah.mathmind.ui.theme.NeuColors

/**
 * Difficulty selection screen for the Math Challenge game.
 * Neubrutalism design with bold cards and solid shadows.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MathChallengeScreen(navController: NavHostController) {
    DifficultySelectScreen(
        title = "Math Challenge",
        descriptions = mapOf(
            "easy" to "Single-digit operations",
            "medium" to "Double-digit operations",
            "hard" to "Mixed multi-digit operations"
        ),
        navController = navController,
        onSelect = { difficulty -> navController.navigate(Routes.mathGame(difficulty)) }
    )
}
