package com.novah.mathmind.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database for NovahMathMind. Stores custom HTML games.
 * Uses singleton pattern to prevent multiple database instances.
 */
@Database(entities = [CustomGame::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customGameDao(): CustomGameDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /** Returns the singleton database instance, creating it if necessary. */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "novah_mathmind_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
