package com.novah.mathmind.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.novah.mathmind.ui.navigation.Routes

/**
 * Difficulty selection screen for the Math Challenge game.
 * Presents Easy, Medium, and Hard options as large tappable buttons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MathChallengeScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Math Challenge") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select Difficulty",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Easy difficulty button
            DifficultyButton(
                text = "Easy",
                description = "Single-digit operations",
                onClick = { navController.navigate(Routes.mathGame("easy")) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Medium difficulty button
            DifficultyButton(
                text = "Medium",
                description = "Double-digit operations",
                onClick = { navController.navigate(Routes.mathGame("medium")) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Hard difficulty button
            DifficultyButton(
                text = "Hard",
                description = "Mixed multi-digit operations",
                onClick = { navController.navigate(Routes.mathGame("hard")) }
            )
        }
    }
}

/**
 * A styled button for selecting a difficulty level.
 */
@Composable
private fun DifficultyButton(text: String, description: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = text, style = MaterialTheme.typography.titleLarge)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
        }
    }
}
