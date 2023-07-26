package com.deenislam.sdk.service.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userpref")
data class UserPref(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id", defaultValue = "1")
    val id:Int = 1,

    @ColumnInfo(name = "language", defaultValue = "en")
    val language:String = "en",

    @ColumnInfo(name = "location_setting", defaultValue = "0")
    val location_setting:Boolean = false,

    @ColumnInfo(name = "token", defaultValue = "")
    var token: String? = "",

    @ColumnInfo(name = "username", defaultValue = "")
    var username:String? = "",

    @ColumnInfo(name = "refresh_token", defaultValue = "")
    var refresh_token:String? = "",

    @ColumnInfo(name = "location_city", defaultValue = "")
    var location_city:String? = "",

    @ColumnInfo(name = "location_country", defaultValue = "")
    var location_country:String? = "",

    @ColumnInfo(name = "tasbeeh_sound", defaultValue = "1")
    var tasbeeh_sound:Boolean = true

)
