package com.example.android.movieapplication.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Movie::class, MovieRemoteKeys::class,
        TvShow::class, TvShowRemoteKeys::class],
    version = 5,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun moviesDao(): MoviesDao
    abstract fun movieRemoteKeysDao(): MovieRemoteKeysDao

    abstract fun tvShowsDao(): TvShowsDao
    abstract fun tvShowRemoteKeysDao(): TvShowRemoteKeysDao

}