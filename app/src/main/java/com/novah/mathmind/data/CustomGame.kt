package com.novah.mathmind.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a custom HTML game.
 * Stores the game title, raw HTML/CSS/JS content, and creator information.
 * The creatorId tracks which user created the game for delete permission.
 */
@Entity(tableName = "custom_games")
data class CustomGame(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val htmlContent: String,
    val cssContent: String,
    val jsContent: String,
    val creatorId: String = "",
    val firebaseId: String = ""
)
