package com.example.android.movieapplication.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FilterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(filter: Filter)

    @Query("SELECT * FROM filter LIMIT 1")
    fun liveFilter(): LiveData<Filter?>

    @Query("SELECT * FROM filter LIMIT 1")
    suspend fun filter(): Filter?
}