package com.novah.mathmind.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a custom HTML game added via Developer Mode.
 * Stores the game title and raw HTML/CSS/JS content for WebView injection.
 */
@Entity(tableName = "custom_games")
data class CustomGame(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val htmlContent: String,
    val cssContent: String,
    val jsContent: String
)
