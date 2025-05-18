package com.example.mc_assignment3.data.local

import androidx.room.TypeConverter
import com.example.mc_assignment3.data.model.Coordinates
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Type converters for Room database to handle complex data types.
 */
class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromCoordinates(coordinates: Coordinates?): String? {
        return coordinates?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toCoordinates(json: String?): Coordinates? {
        return json?.let {
            gson.fromJson(it, Coordinates::class.java)
        }
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}