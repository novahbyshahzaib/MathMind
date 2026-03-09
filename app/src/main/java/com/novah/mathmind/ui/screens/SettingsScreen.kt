package com.novah.mathmind.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.novah.mathmind.ui.navigation.Routes

/**
 * Settings page with a hidden Easter Egg to unlock Developer Mode.
 *
 * Easter Egg Logic:
 * - A "Coded by novah" text is placed at the bottom of the screen.
 * - Tapping it 5 times within a 2-second window triggers a passcode dialog.
 * - Entering "NOVAH HOST" unlocks Developer Options.
 * - The unlocked state is persisted in SharedPreferences.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    // Track whether Developer Mode is unlocked
    var devModeUnlocked by remember {
        mutableStateOf(prefs.getBoolean("dev_mode_unlocked", false))
    }

    // Easter Egg tap tracking
    var tapTimestamps by remember { mutableStateOf(listOf<Long>()) }
    var showPasscodeDialog by remember { mutableStateOf(false) }
    var passcodeInput by remember { mutableStateOf("") }
    var passcodeError by remember { mutableStateOf(false) }

    /** Handles taps on the "Coded by novah" text for Easter Egg */
    fun onEasterEggTap() {
        val now = System.currentTimeMillis()
        // Keep only taps within the last 2 seconds
        val recentTaps = tapTimestamps.filter { now - it < 2000L } + now
        tapTimestamps = recentTaps

        // If 5 taps within 2 seconds, show the passcode dialog
        if (recentTaps.size >= 5) {
            tapTimestamps = emptyList()
            if (!devModeUnlocked) {
                showPasscodeDialog = true
            }
        }
    }

    // Passcode dialog
    if (showPasscodeDialog) {
        AlertDialog(
            onDismissRequest = {
                showPasscodeDialog = false
                passcodeInput = ""
                passcodeError = false
            },
            title = { Text("Enter Developer Code") },
            text = {
                Column {
                    OutlinedTextField(
                        value = passcodeInput,
                        onValueChange = {
                            passcodeInput = it
                            passcodeError = false
                        },
                        label = { Text("Code") },
                        singleLine = true,
                        isError = passcodeError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (passcodeError) {
                        Text(
                            text = "Incorrect code.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (passcodeInput == "NOVAH HOST") {
                        // Unlock Developer Mode and save to SharedPreferences
                        devModeUnlocked = true
                        prefs.edit().putBoolean("dev_mode_unlocked", true).apply()
                        showPasscodeDialog = false
                        passcodeInput = ""
                    } else {
                        passcodeError = true
                    }
                }) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPasscodeDialog = false
                    passcodeInput = ""
                    passcodeError = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
        ) {
            // Settings items
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                // App Info section
                Text(
                    text = "App Info",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                ListItem(
                    headlineContent = { Text("Version") },
                    supportingContent = { Text("1.0.0") },
                    leadingContent = {
                        Icon(Icons.Default.Info, contentDescription = null)
                    }
                )

                HorizontalDivider()

                ListItem(
                    headlineContent = { Text("App Name") },
                    supportingContent = { Text("NovahMathMind") },
                    leadingContent = {
                        Icon(Icons.Default.Games, contentDescription = null)
                    }
                )

                HorizontalDivider()

                // Developer Options - only shown when unlocked
                if (devModeUnlocked) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Developer Options",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    ListItem(
                        headlineContent = { Text("Add Custom Game") },
                        supportingContent = { Text("Inject HTML/CSS/JS games") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Code,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        },
                        modifier = Modifier.clickable {
                            navController.navigate(Routes.ADD_CUSTOM_GAME)
                        }
                    )

                    HorizontalDivider()
                }
            }

            // Easter Egg: "Coded by novah" text at the absolute bottom
            Text(
                text = "Coded by novah",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEasterEggTap() }
                    .padding(16.dp)
            )
        }
    }
}
