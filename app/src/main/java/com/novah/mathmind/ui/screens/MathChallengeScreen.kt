package com.novah.mathmind.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.novah.mathmind.ui.navigation.Routes

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
