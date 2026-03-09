package com.novah.mathmind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.novah.mathmind.data.AppDatabase
import com.novah.mathmind.data.CustomGame
import com.novah.mathmind.data.FirebaseGameRepository
import com.novah.mathmind.ui.navigation.Routes
import com.novah.mathmind.ui.theme.NeuColors
import kotlinx.coroutines.launch

/**
 * Data class representing a game card on the homepage.
 */
data class GameItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val description: String,
    val cardColor: Color = NeuColors.Yellow
)

/**
 * Homepage Dashboard displaying all available games in a neubrutalism grid layout.
 * Shows native games and shared Firebase games with delete support.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, database: AppDatabase) {
    val firebaseRepo = remember { FirebaseGameRepository() }
    val scope = rememberCoroutineScope()

    // Collect shared games from Firebase reactively
    val firebaseGames by firebaseRepo.getAllGames().collectAsState(initial = emptyList())

    // Get current user ID for delete permission check
    var currentUserId by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        try {
            currentUserId = firebaseRepo.getCurrentUserId()
        } catch (_: Exception) {}
    }

    // Delete confirmation dialog state
    var gameToDelete by remember { mutableStateOf<CustomGame?>(null) }
    var showDeleteError by remember { mutableStateOf(false) }

    // Native games list with neubrutalism colors
    val nativeGames = listOf(
        GameItem(
            title = "Math Challenge",
            icon = Icons.Default.Calculate,
            route = Routes.MATH_CHALLENGE,
            description = "Test your math skills!",
            cardColor = NeuColors.Yellow
        ),
        GameItem(
            title = "Sudoku",
            icon = Icons.Default.GridOn,
            route = Routes.SUDOKU_SELECT,
            description = "Classic 9×9 puzzle",
            cardColor = NeuColors.Blue
        ),
        GameItem(
            title = "Reaction Time",
            icon = Icons.Default.Speed,
            route = Routes.REACTION_SELECT,
            description = "Test your reflexes!",
            cardColor = NeuColors.Pink
        ),
        GameItem(
            title = "Number Memory",
            icon = Icons.Default.Psychology,
            route = Routes.NUMBER_MEMORY_SELECT,
            description = "Remember the numbers!",
            cardColor = NeuColors.Green
        )
    )

    // Delete confirmation dialog
    gameToDelete?.let { game ->
        AlertDialog(
            onDismissRequest = { gameToDelete = null },
            title = {
                Text(
                    "Delete Game",
                    fontWeight = FontWeight.Black
                )
            },
            text = {
                Text("Are you sure you want to delete \"${game.title}\"?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                if (game.firebaseId.isNotEmpty()) {
                                    if (!firebaseRepo.deleteGame(game.firebaseId)) {
                                        showDeleteError = true
                                    }
                                }
                                database.customGameDao().deleteGame(game)
                            } catch (_: Exception) {
                                showDeleteError = true
                            }
                            gameToDelete = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF4444)
                    ),
                    modifier = Modifier
                        .border(2.dp, NeuColors.Black, RoundedCornerShape(8.dp))
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { gameToDelete = null },
                    modifier = Modifier.border(2.dp, NeuColors.Black, RoundedCornerShape(8.dp))
                ) {
                    Text("Cancel", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.border(3.dp, NeuColors.Black, RoundedCornerShape(12.dp))
        )
    }

    // Permission error snackbar
    if (showDeleteError) {
        AlertDialog(
            onDismissRequest = { showDeleteError = false },
            title = { Text("Cannot Delete", fontWeight = FontWeight.Bold) },
            text = { Text("You can only delete games that you created.") },
            confirmButton = {
                TextButton(onClick = { showDeleteError = false }) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.border(3.dp, NeuColors.Black, RoundedCornerShape(12.dp))
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "NovahMathMind",
                        fontWeight = FontWeight.Black
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NeuColors.Yellow,
                    titleContentColor = NeuColors.Black,
                    actionIconContentColor = NeuColors.Black
                ),
                modifier = Modifier
                    .border(
                        width = 3.dp,
                        color = NeuColors.Black
                    )
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Native game cards
            items(nativeGames) { game ->
                NeuGameCard(
                    title = game.title,
                    icon = game.icon,
                    description = game.description,
                    cardColor = game.cardColor,
                    onClick = { navController.navigate(game.route) }
                )
            }

            // Firebase shared game cards
            items(firebaseGames) { customGame ->
                NeuGameCard(
                    title = customGame.title,
                    icon = Icons.Default.Code,
                    description = "Custom Game",
                    cardColor = NeuColors.Purple,
                    showDelete = customGame.creatorId == currentUserId,
                    onDelete = { gameToDelete = customGame },
                    onClick = {
                        navController.navigate(
                            Routes.customGamePlayer(customGame.firebaseId)
                        )
                    }
                )
            }
        }
    }
}

/**
 * Neubrutalism-styled game card with bold borders and solid shadow.
 */
@Composable
fun NeuGameCard(
    title: String,
    icon: ImageVector,
    description: String,
    cardColor: Color = NeuColors.Yellow,
    showDelete: Boolean = false,
    onDelete: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)

    Box(modifier = Modifier.fillMaxWidth()) {
        // Solid shadow layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .offset(x = 4.dp, y = 4.dp)
                .clip(shape)
                .background(NeuColors.Black)
        )
        // Card layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(shape)
                .background(cardColor)
                .border(3.dp, NeuColors.Black, shape)
                .clickable { onClick() }
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
                    tint = NeuColors.Black
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    color = NeuColors.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = NeuColors.Black.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold
                )
            }

            // Delete button (only shown for creator)
            if (showDelete && onDelete != null) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .background(
                            Color(0xFFFF4444),
                            RoundedCornerShape(8.dp)
                        )
                        .border(2.dp, NeuColors.Black, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// Keep backward compatibility alias
@Composable
fun GameCard(
    title: String,
    icon: ImageVector,
    description: String,
    onClick: () -> Unit
) {
    NeuGameCard(
        title = title,
        icon = icon,
        description = description,
        onClick = onClick
    )
}
