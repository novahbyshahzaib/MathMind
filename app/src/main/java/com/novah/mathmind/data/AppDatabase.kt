package com.novah.mathmind.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room database for NovahMathMind. Stores custom HTML games.
 * Uses singleton pattern to prevent multiple database instances.
 */
@Database(entities = [CustomGame::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customGameDao(): CustomGameDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE custom_games ADD COLUMN creatorId TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE custom_games ADD COLUMN firebaseId TEXT NOT NULL DEFAULT ''")
            }
        }

        /** Returns the singleton database instance, creating it if necessary. */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "novah_mathmind_database"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
