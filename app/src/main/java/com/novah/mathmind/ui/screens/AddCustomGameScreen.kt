package com.novah.mathmind.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.novah.mathmind.data.AppDatabase
import com.novah.mathmind.data.CustomGame
import kotlinx.coroutines.launch

/**
 * Screen for adding a custom HTML game via Developer Mode.
 * Provides input fields for game title, raw HTML, CSS, and JS content.
 * Saves the custom game to Room database for display on the homepage.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomGameScreen(navController: NavHostController, database: AppDatabase) {
    var title by remember { mutableStateOf("") }
    var htmlContent by remember { mutableStateOf("") }
    var cssContent by remember { mutableStateOf("") }
    var jsContent by remember { mutableStateOf("") }
    var saveSuccess by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Custom Game") },
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
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Game Title input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Game Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // HTML content text area
            Text(
                text = "HTML Content",
                style = MaterialTheme.typography.titleSmall
            )
            OutlinedTextField(
                value = htmlContent,
                onValueChange = { htmlContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text("<div>Your HTML here...</div>") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CSS content text area
            Text(
                text = "CSS Content",
                style = MaterialTheme.typography.titleSmall
            )
            OutlinedTextField(
                value = cssContent,
                onValueChange = { cssContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text("body { background: #000; }") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // JavaScript content text area
            Text(
                text = "JavaScript Content",
                style = MaterialTheme.typography.titleSmall
            )
            OutlinedTextField(
                value = jsContent,
                onValueChange = { jsContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text("console.log('Hello!');") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        scope.launch {
                            database.customGameDao().insertGame(
                                CustomGame(
                                    title = title.trim(),
                                    htmlContent = htmlContent,
                                    cssContent = cssContent,
                                    jsContent = jsContent
                                )
                            )
                            saveSuccess = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text("Save Game")
            }

            // Success message
            if (saveSuccess) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "✓ Game saved successfully! It will appear on the homepage.",
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
