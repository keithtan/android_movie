package com.example.android.movieapplication.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Movie::class, RemoteKeys::class],
    version = 4,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun moviesDao(): MoviesDao
    abstract fun remoteKeysDao(): RemoteKeysDao

}