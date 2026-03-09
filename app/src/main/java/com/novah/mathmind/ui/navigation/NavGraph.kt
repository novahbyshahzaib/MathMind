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
    const val SUDOKU_SELECT = "sudoku_select"
    const val SUDOKU = "sudoku/{difficulty}"
    const val REACTION_SELECT = "reaction_select"
    const val REACTION_TIME = "reaction_time/{difficulty}"
    const val NUMBER_MEMORY_SELECT = "number_memory_select"
    const val NUMBER_MEMORY = "number_memory/{difficulty}"
    const val SETTINGS = "settings"
    const val ADD_CUSTOM_GAME = "add_custom_game"
    const val CUSTOM_GAME_PLAYER = "custom_game_player/{gameId}"

    fun mathGame(difficulty: String) = "math_game/$difficulty"
    fun sudoku(difficulty: String) = "sudoku/$difficulty"
    fun reactionTime(difficulty: String) = "reaction_time/$difficulty"
    fun numberMemory(difficulty: String) = "number_memory/$difficulty"
    fun customGamePlayer(gameId: String) = "custom_game_player/$gameId"
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

        // Sudoku difficulty selection
        composable(Routes.SUDOKU_SELECT) {
            DifficultySelectScreen(
                title = "Sudoku",
                descriptions = mapOf(
                    "easy" to "30 cells removed",
                    "medium" to "40 cells removed",
                    "hard" to "55 cells removed"
                ),
                navController = navController,
                onSelect = { difficulty -> navController.navigate(Routes.sudoku(difficulty)) }
            )
        }

        // Sudoku game with difficulty
        composable(
            route = Routes.SUDOKU,
            arguments = listOf(navArgument("difficulty") { type = NavType.StringType })
        ) { backStackEntry ->
            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "medium"
            SudokuScreen(navController = navController, difficulty = difficulty)
        }

        // Reaction Time difficulty selection
        composable(Routes.REACTION_SELECT) {
            DifficultySelectScreen(
                title = "Reaction Time",
                descriptions = mapOf(
                    "easy" to "Longer wait time, relaxed",
                    "medium" to "Standard timing",
                    "hard" to "Short wait, stay sharp!"
                ),
                navController = navController,
                onSelect = { difficulty -> navController.navigate(Routes.reactionTime(difficulty)) }
            )
        }

        // Reaction Time game with difficulty
        composable(
            route = Routes.REACTION_TIME,
            arguments = listOf(navArgument("difficulty") { type = NavType.StringType })
        ) { backStackEntry ->
            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "medium"
            ReactionTimeScreen(navController = navController, difficulty = difficulty)
        }

        // Number Memory difficulty selection
        composable(Routes.NUMBER_MEMORY_SELECT) {
            DifficultySelectScreen(
                title = "Number Memory",
                descriptions = mapOf(
                    "easy" to "Start with 3 digits, slow pace",
                    "medium" to "Start with 4 digits, normal",
                    "hard" to "Start with 5 digits, fast!"
                ),
                navController = navController,
                onSelect = { difficulty -> navController.navigate(Routes.numberMemory(difficulty)) }
            )
        }

        // Number Memory game with difficulty
        composable(
            route = Routes.NUMBER_MEMORY,
            arguments = listOf(navArgument("difficulty") { type = NavType.StringType })
        ) { backStackEntry ->
            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "medium"
            NumberMemoryScreen(navController = navController, difficulty = difficulty)
        }

        // Settings page
        composable(Routes.SETTINGS) {
            SettingsScreen(navController = navController)
        }

        // Add Custom Game screen
        composable(Routes.ADD_CUSTOM_GAME) {
            AddCustomGameScreen(navController = navController, database = database)
        }

        // Custom game WebView player (uses Firebase ID as string)
        composable(
            route = Routes.CUSTOM_GAME_PLAYER,
            arguments = listOf(navArgument("gameId") { type = NavType.StringType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
            CustomGamePlayerScreen(gameId = gameId, database = database, navController = navController)
        }
    }
}
