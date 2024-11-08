package ru.vasmarfas.cinewave.data.db.entity

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import ru.vasmarfas.cinewave.data.retrofit.entity.Filters
import ru.vasmarfas.cinewave.data.retrofit.entity.ImageItem

class DataConverter {
    @TypeConverter
    fun fromFilters(value: Filters): String {
        val gson = Gson()
        val type = object : TypeToken<Filters>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toFilters(value: String): Filters {
        val gson = Gson()
        val type = object : TypeToken<Filters>() {}.type
        return gson.fromJson(value, type)
    }
    @TypeConverter
    fun fromImageItem(value: ImageItem): String {
        val gson = Gson()
        val type = object : TypeToken<ImageItem>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toImageItem(value: String): ImageItem {
        val gson = Gson()
        val type = object : TypeToken<ImageItem>() {}.type
        return gson.fromJson(value, type)
    }
    @TypeConverter
    fun fromIntList(intList: List<Int>): String {
        return intList.joinToString(",")
    }

    @TypeConverter
    fun toIntList(data: String): List<Int> {
        return if (data == "") {
            emptyList()
        } else {
            data.split(",").map { it.toInt() }
        }
    }
}
