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
import com.novah.mathmind.ui.theme.NeuColors

/**
 * Reusable neubrutalism-styled difficulty selection screen.
 * Presents Easy, Medium, and Hard options as bold cards with solid shadows.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DifficultySelectScreen(
    title: String,
    descriptions: Map<String, String>,
    navController: NavHostController,
    onSelect: (String) -> Unit
) {
    val difficulties = listOf(
        Triple("easy", "Easy", NeuColors.Green),
        Triple("medium", "Medium", NeuColors.Yellow),
        Triple("hard", "Hard", NeuColors.Pink)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(title, fontWeight = FontWeight.Black)
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
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(32.dp))

            difficulties.forEach { (key, label, color) ->
                NeuDifficultyButton(
                    text = label,
                    description = descriptions[key] ?: "",
                    cardColor = color,
                    onClick = { onSelect(key) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Neubrutalism-styled difficulty selection button with solid shadow.
 */
@Composable
private fun NeuDifficultyButton(
    text: String,
    description: String,
    cardColor: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)

    Box(modifier = Modifier.fillMaxWidth()) {
        // Shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .offset(x = 4.dp, y = 4.dp)
                .clip(shape)
                .background(NeuColors.Black)
        )
        // Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(shape)
                .background(cardColor)
                .border(3.dp, NeuColors.Black, shape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = NeuColors.Black
                )
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = NeuColors.Black.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
