package com.novah.mathmind.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for custom games stored in Room database.
 */
@Dao
interface CustomGameDao {
    /** Returns a Flow of all custom games, updating the UI reactively. */
    @Query("SELECT * FROM custom_games ORDER BY id ASC")
    fun getAllGames(): Flow<List<CustomGame>>

    /** Inserts a new custom game into the database. */
    @Insert
    suspend fun insertGame(game: CustomGame)

    /** Retrieves a single custom game by its ID. */
    @Query("SELECT * FROM custom_games WHERE id = :id LIMIT 1")
    suspend fun getGameById(id: Int): CustomGame?

    /** Deletes a custom game from the database. */
    @Delete
    suspend fun deleteGame(game: CustomGame)
}
