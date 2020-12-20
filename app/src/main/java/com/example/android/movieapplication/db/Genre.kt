package com.example.android.movieapplication.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genres")
data class Genre(
    @PrimaryKey val id: Int,
    val name: String,
    var included: Boolean,
    var excluded: Boolean
)