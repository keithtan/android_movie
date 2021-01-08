package com.example.android.movieapplication.data

import androidx.datastore.CorruptionException
import androidx.datastore.Serializer
import com.example.android.movieapplication.MovieFilterPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object MovieFilterPreferencesSerializer : Serializer<MovieFilterPreferences> {

//    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    override fun readFrom(input: InputStream): MovieFilterPreferences {
        try {
            return MovieFilterPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override fun writeTo(t: MovieFilterPreferences, output: OutputStream) = t.writeTo(output)
}