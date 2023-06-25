package com.deenislam.sdk.service.database

import androidx.room.TypeConverter
import com.deenislam.sdk.service.models.MenuModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

internal class Converters {

    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {

        }.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

}