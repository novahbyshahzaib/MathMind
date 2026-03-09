package com.novah.mathmind.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.novah.mathmind.data.AppDatabase
import com.novah.mathmind.ui.screens.*

/**
 * Defines all navigation routes used in the app.
 */
object Routes {
    const val HOME = "home"
    const val MATH_CHALLENGE = "math_challenge"
    const val MATH_GAME = "math_game/{difficulty}"
    const val SUDOKU = "sudoku"
    const val REACTION_TIME = "reaction_time"
    const val SETTINGS = "settings"
    const val ADD_CUSTOM_GAME = "add_custom_game"
    const val CUSTOM_GAME_PLAYER = "custom_game_player/{gameId}"

    fun mathGame(difficulty: String) = "math_game/$difficulty"
    fun customGamePlayer(gameId: Int) = "custom_game_player/$gameId"
}

/**
 * Main navigation graph connecting all screens in the app.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    database: AppDatabase
) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        // Homepage dashboard
        composable(Routes.HOME) {
            HomeScreen(navController = navController, database = database)
        }

        // Math Challenge difficulty selection
        composable(Routes.MATH_CHALLENGE) {
            MathChallengeScreen(navController = navController)
        }

        // Math Challenge gameplay with difficulty parameter
        composable(
            route = Routes.MATH_GAME,
            arguments = listOf(navArgument("difficulty") { type = NavType.StringType })
        ) { backStackEntry ->
            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "easy"
            MathGameScreen(difficulty = difficulty, navController = navController)
        }

        // Sudoku game
        composable(Routes.SUDOKU) {
            SudokuScreen(navController = navController)
        }

        // Reaction Time game
        composable(Routes.REACTION_TIME) {
            ReactionTimeScreen(navController = navController)
        }

        // Settings page with Easter Egg
        composable(Routes.SETTINGS) {
            SettingsScreen(navController = navController)
        }

        // Add Custom Game screen (Developer Mode)
        composable(Routes.ADD_CUSTOM_GAME) {
            AddCustomGameScreen(navController = navController, database = database)
        }

        // Custom game WebView player
        composable(
            route = Routes.CUSTOM_GAME_PLAYER,
            arguments = listOf(navArgument("gameId") { type = NavType.IntType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getInt("gameId") ?: 0
            CustomGamePlayerScreen(gameId = gameId, database = database, navController = navController)
        }
    }
}
