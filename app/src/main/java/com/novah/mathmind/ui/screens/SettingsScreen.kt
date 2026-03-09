package com.novah.mathmind.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.novah.mathmind.ui.navigation.Routes
import com.novah.mathmind.ui.theme.NeuColors

/**
 * Settings page with neubrutalism design and Developer Mode Easter Egg.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    var devModeUnlocked by remember {
        mutableStateOf(prefs.getBoolean("dev_mode_unlocked", false))
    }

    var tapTimestamps by remember { mutableStateOf(listOf<Long>()) }
    var showPasscodeDialog by remember { mutableStateOf(false) }
    var passcodeInput by remember { mutableStateOf("") }
    var passcodeError by remember { mutableStateOf(false) }

    fun onEasterEggTap() {
        val now = System.currentTimeMillis()
        val recentTaps = tapTimestamps.filter { now - it < 2000L } + now
        tapTimestamps = recentTaps
        if (recentTaps.size >= 5) {
            tapTimestamps = emptyList()
            if (!devModeUnlocked) {
                showPasscodeDialog = true
            }
        }
    }

    // Neubrutalism passcode dialog
    if (showPasscodeDialog) {
        AlertDialog(
            onDismissRequest = {
                showPasscodeDialog = false
                passcodeInput = ""
                passcodeError = false
            },
            title = {
                Text("Enter Developer Code", fontWeight = FontWeight.Black)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = passcodeInput,
                        onValueChange = {
                            passcodeInput = it
                            passcodeError = false
                        },
                        label = { Text("Code", fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        isError = passcodeError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(3.dp, NeuColors.Black, RoundedCornerShape(8.dp))
                    )
                    if (passcodeError) {
                        Text(
                            text = "Incorrect code.",
                            color = NeuColors.Pink,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (passcodeInput == "NOVAH HOST") {
                        devModeUnlocked = true
                        prefs.edit().putBoolean("dev_mode_unlocked", true).apply()
                        showPasscodeDialog = false
                        passcodeInput = ""
                    } else {
                        passcodeError = true
                    }
                }) {
                    Text("Submit", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPasscodeDialog = false
                    passcodeInput = ""
                    passcodeError = false
                }) {
                    Text("Cancel", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.border(3.dp, NeuColors.Black, RoundedCornerShape(12.dp))
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Black) },
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
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                // App Info section
                Text(
                    text = "App Info",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                val itemShape = RoundedCornerShape(8.dp)

                // Version item
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(itemShape)
                        .background(NeuColors.LightGray)
                        .border(2.dp, NeuColors.Black, itemShape)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = NeuColors.Black)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Version", fontWeight = FontWeight.Bold)
                            Text("1.0.0", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // App Name item
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(itemShape)
                        .background(NeuColors.LightGray)
                        .border(2.dp, NeuColors.Black, itemShape)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Games, contentDescription = null, tint = NeuColors.Black)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("App Name", fontWeight = FontWeight.Bold)
                            Text("NovahMathMind", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // Developer Options
                if (devModeUnlocked) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Developer Options",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = NeuColors.Purple,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(itemShape)
                            .background(NeuColors.Purple.copy(alpha = 0.2f))
                            .border(2.dp, NeuColors.Black, itemShape)
                            .clickable { navController.navigate(Routes.ADD_CUSTOM_GAME) }
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Code,
                                contentDescription = null,
                                tint = NeuColors.Black
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Add Custom Game",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Inject HTML/CSS/JS games",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // Easter Egg text
            Text(
                text = "Coded by novah",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEasterEggTap() }
                    .padding(16.dp)
            )
        }
    }
}
