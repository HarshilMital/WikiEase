package com.example.mc_assignment3.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mc_assignment3.data.model.WikipediaArticle

/**
 * Room database for storing Wikipedia articles locally.
 * This provides offline access to cached articles.
 */
@Database(entities = [WikipediaArticle::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WikipediaDatabase : RoomDatabase() {

    abstract fun wikipediaDao(): WikipediaDao

    companion object {
        @Volatile
        private var INSTANCE: WikipediaDatabase? = null

        fun getDatabase(context: Context): WikipediaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WikipediaDatabase::class.java,
                    "wikipedia_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}