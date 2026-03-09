package com.novah.mathmind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.novah.mathmind.data.AppDatabase
import com.novah.mathmind.ui.navigation.AppNavGraph
import com.novah.mathmind.ui.theme.NovahMathMindTheme

/**
 * Main entry point for the NovahMathMind application.
 * Sets up the Compose UI, navigation controller, and Room database.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the Room database singleton
        val database = AppDatabase.getDatabase(applicationContext)

        setContent {
            NovahMathMindTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    AppNavGraph(
                        navController = navController,
                        database = database
                    )
                }
            }
        }
    }
}
