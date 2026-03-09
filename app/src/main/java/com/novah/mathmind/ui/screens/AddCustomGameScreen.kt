package com.novah.mathmind.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.novah.mathmind.data.AppDatabase
import com.novah.mathmind.data.CustomGame
import com.novah.mathmind.data.FirebaseGameRepository
import com.novah.mathmind.ui.theme.NeuColors
import kotlinx.coroutines.launch

/**
 * Screen for adding a custom HTML game. Saves to both local Room DB and Firebase Firestore.
 * Neubrutalism design with bold borders and solid shadows.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomGameScreen(navController: NavHostController, database: AppDatabase) {
    var title by remember { mutableStateOf("") }
    var htmlContent by remember { mutableStateOf("") }
    var cssContent by remember { mutableStateOf("") }
    var jsContent by remember { mutableStateOf("") }
    var saveSuccess by remember { mutableStateOf(false) }
    var saveError by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val firebaseRepo = remember { FirebaseGameRepository() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Custom Game", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NeuColors.Purple,
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
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Game Title
            Text(
                text = "Game Title",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Enter game title") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(3.dp, NeuColors.Black, RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // HTML
            Text(
                text = "HTML Content",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = htmlContent,
                onValueChange = { htmlContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .border(3.dp, NeuColors.Black, RoundedCornerShape(8.dp)),
                placeholder = { Text("<div>Your HTML here...</div>") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CSS
            Text(
                text = "CSS Content",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = cssContent,
                onValueChange = { cssContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .border(3.dp, NeuColors.Black, RoundedCornerShape(8.dp)),
                placeholder = { Text("body { background: #000; }") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // JavaScript
            Text(
                text = "JavaScript Content",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = jsContent,
                onValueChange = { jsContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .border(3.dp, NeuColors.Black, RoundedCornerShape(8.dp)),
                placeholder = { Text("console.log('Hello!');") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save button
            NeuButton(
                text = "Save Game",
                color = NeuColors.Green,
                onClick = {
                    if (title.isNotBlank()) {
                        scope.launch {
                            try {
                                val game = CustomGame(
                                    title = title.trim(),
                                    htmlContent = htmlContent,
                                    cssContent = cssContent,
                                    jsContent = jsContent
                                )
                                // Save to Firebase for all users
                                val firebaseId = firebaseRepo.addGame(game)
                                // Also save locally with firebaseId
                                database.customGameDao().insertGame(
                                    game.copy(
                                        firebaseId = firebaseId,
                                        creatorId = firebaseRepo.getCurrentUserId()
                                    )
                                )
                                saveSuccess = true
                                saveError = ""
                            } catch (e: Exception) {
                                // If Firebase fails, save locally only
                                try {
                                    database.customGameDao().insertGame(
                                        CustomGame(
                                            title = title.trim(),
                                            htmlContent = htmlContent,
                                            cssContent = cssContent,
                                            jsContent = jsContent
                                        )
                                    )
                                    saveSuccess = true
                                    saveError = "Saved locally (Firebase unavailable)"
                                } catch (e2: Exception) {
                                    saveError = "Failed to save: ${e2.message}"
                                }
                            }
                        }
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            // Feedback messages
            if (saveSuccess) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "✓ Game saved! It will appear on all users' homepages.",
                    color = NeuColors.Green,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            if (saveError.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = saveError,
                    color = NeuColors.Orange,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
