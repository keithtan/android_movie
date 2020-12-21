package com.example.android.movieapplication.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Movie::class, RemoteKeys::class, Filter::class, Genre::class],
    version = 2,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun moviesDao(): MoviesDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun filterDao(): FilterDao
    abstract fun genresDao(): GenresDao

    companion object {

        @Volatile
        private var INSTANCE: MovieDatabase? = null

        fun getInstance(context: Context): MovieDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                MovieDatabase::class.java, "movie-db"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}