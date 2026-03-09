package com.novah.mathmind.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.novah.mathmind.data.AppDatabase
import com.novah.mathmind.data.CustomGame

/**
 * Full-screen WebView player for custom HTML games.
 * Loads the game's HTML, CSS, and JS from Room database and injects
 * them into a WebView for playback. JavaScript is fully enabled.
 */
@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomGamePlayerScreen(
    gameId: Int,
    database: AppDatabase,
    navController: NavHostController
) {
    // Load the custom game data from the database
    var game by remember { mutableStateOf<CustomGame?>(null) }
    LaunchedEffect(gameId) {
        game = database.customGameDao().getGameById(gameId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(game?.title ?: "Custom Game") },
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
        game?.let { currentGame ->
            // Build the full HTML document by combining stored HTML, CSS, and JS
            val fullHtml = buildString {
                append("<!DOCTYPE html>")
                append("<html><head>")
                append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
                append("<style>")
                append(currentGame.cssContent)
                append("</style>")
                append("</head><body>")
                append(currentGame.htmlContent)
                append("<script>")
                append(currentGame.jsContent)
                append("</script>")
                append("</body></html>")
            }

            // Full-screen WebView displaying the custom game
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        // Enable JavaScript for game interactivity
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        // Restrict file and content access for security
                        settings.allowFileAccess = false
                        settings.allowContentAccess = false
                        webViewClient = WebViewClient()
                        // Load the constructed HTML directly into the WebView
                        loadDataWithBaseURL(
                            null,
                            fullHtml,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } ?: run {
            // Show loading indicator while game data is being fetched
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
