package com.novah.mathmind.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.novah.mathmind.data.AppDatabase
import com.novah.mathmind.data.CustomGame
import com.novah.mathmind.data.FirebaseGameRepository
import com.novah.mathmind.ui.theme.NeuColors

/**
 * Full-screen WebView player for custom HTML games.
 * Loads game from Firebase Firestore by ID. Falls back to local Room DB.
 * Fixed WebView settings for proper HTML/CSS/JS rendering.
 */
@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomGamePlayerScreen(
    gameId: String,
    database: AppDatabase,
    navController: NavHostController
) {
    val firebaseRepo = remember { FirebaseGameRepository() }
    var game by remember { mutableStateOf<CustomGame?>(null) }
    var loadError by remember { mutableStateOf(false) }

    // Try loading from Firebase first, then fall back to local DB
    LaunchedEffect(gameId) {
        try {
            game = firebaseRepo.getGameById(gameId)
        } catch (_: Exception) {}

        if (game == null) {
            // Fall back to local database using numeric ID
            val numericId = gameId.toIntOrNull()
            if (numericId != null) {
                game = database.customGameDao().getGameById(numericId)
            }
        }

        if (game == null) loadError = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        game?.title ?: "Custom Game",
                        fontWeight = FontWeight.Black
                    )
                },
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
        game?.let { currentGame ->
            // Build a proper HTML document with complete structure
            val fullHtml = buildString {
                append("<!DOCTYPE html>")
                append("<html lang=\"en\">")
                append("<head>")
                append("<meta charset=\"UTF-8\">")
                append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\">")
                append("<style>")
                // Reset styles for consistent rendering
                append("* { margin: 0; padding: 0; box-sizing: border-box; } ")
                append("html, body { width: 100%; height: 100%; overflow: auto; -webkit-text-size-adjust: 100%; } ")
                append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; } ")
                // User CSS
                append(currentGame.cssContent)
                append("</style>")
                append("</head>")
                append("<body>")
                append(currentGame.htmlContent)
                append("<script>")
                // Wrap JS in try-catch for error resilience
                append("try { ")
                append(currentGame.jsContent)
                append(" } catch(e) { console.error('Game error:', e); }")
                append("</script>")
                append("</body></html>")
            }

            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        // Enable JavaScript and DOM storage for full interactivity
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.databaseEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true
                        settings.setSupportZoom(false)
                        // Restrict file access for security
                        settings.allowFileAccess = false
                        settings.allowContentAccess = false
                        // Use proper web clients
                        webViewClient = WebViewClient()
                        webChromeClient = WebChromeClient()
                        // Load the HTML content
                        loadDataWithBaseURL(
                            "https://localhost/",
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                if (loadError) {
                    Text(
                        text = "Failed to load game",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
