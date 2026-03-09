package com.novah.mathmind.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.novah.mathmind.data.AppDatabase
import com.novah.mathmind.data.CustomGame
import com.novah.mathmind.ui.navigation.Routes

/**
 * Data class representing a game card on the homepage.
 * @param title Display name of the game
 * @param icon Material icon for the card
 * @param route Navigation route when tapped
 * @param description Short description shown on the card
 */
data class GameItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val description: String
)

/**
 * Homepage Dashboard displaying all available games in a grid layout.
 * Shows native games and dynamically loaded custom HTML games from Room.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, database: AppDatabase) {
    // Collect custom games from Room database reactively
    val customGames by database.customGameDao().getAllGames()
        .collectAsState(initial = emptyList())

    // Native games list
    val nativeGames = listOf(
        GameItem(
            title = "Math Challenge",
            icon = Icons.Default.Calculate,
            route = Routes.MATH_CHALLENGE,
            description = "Test your math skills!"
        ),
        GameItem(
            title = "Sudoku",
            icon = Icons.Default.GridOn,
            route = Routes.SUDOKU,
            description = "Classic 9x9 puzzle"
        ),
        GameItem(
            title = "Reaction Time",
            icon = Icons.Default.Speed,
            route = Routes.REACTION_TIME,
            description = "Test your reflexes!"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "NovahMathMind",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Settings button in the top bar
                    IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Display native game cards
            items(nativeGames) { game ->
                GameCard(
                    title = game.title,
                    icon = game.icon,
                    description = game.description,
                    onClick = { navController.navigate(game.route) }
                )
            }

            // Display custom HTML game cards from the database
            items(customGames) { customGame ->
                GameCard(
                    title = customGame.title,
                    icon = Icons.Default.Code,
                    description = "Custom HTML Game",
                    onClick = {
                        navController.navigate(Routes.customGamePlayer(customGame.id))
                    }
                )
            }
        }
    }
}

/**
 * A Material3 card component for displaying a single game.
 */
@Composable
fun GameCard(
    title: String,
    icon: ImageVector,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
