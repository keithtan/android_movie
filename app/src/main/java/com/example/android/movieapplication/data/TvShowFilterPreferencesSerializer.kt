package com.example.android.movieapplication.data

import androidx.datastore.CorruptionException
import androidx.datastore.Serializer
import com.example.android.movieapplication.MovieFilterPreferences
import com.example.android.movieapplication.TvShowFilterPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object TvShowFilterPreferencesSerializer : Serializer<TvShowFilterPreferences> {

//    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    override fun readFrom(input: InputStream): TvShowFilterPreferences {
        try {
            return TvShowFilterPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override fun writeTo(t: TvShowFilterPreferences, output: OutputStream) = t.writeTo(output)
}