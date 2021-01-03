package com.example.android.movieapplication.ui.movies.custommovies.filter

import androidx.datastore.CorruptionException
import androidx.datastore.Serializer
import com.example.android.movieapplication.UserPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object UserPreferencesSerializer : Serializer<UserPreferences> {

//    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    override fun readFrom(input: InputStream): UserPreferences {
        try {
            return UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override fun writeTo(t: UserPreferences, output: OutputStream) = t.writeTo(output)
}