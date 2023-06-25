package com.deenislam.sdk.service.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_notification")
internal data class PrayerNotification(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", defaultValue = "0")
    val id:Int = 0,

    @ColumnInfo(name = "date", defaultValue = "")
    val date:String="",

    @ColumnInfo(name = "prayer_tag", defaultValue = "")
    val prayer_tag:String,

    @ColumnInfo(name = "state", defaultValue = "1")
    val state:Int = 1,

    @ColumnInfo(name = "sound_file", defaultValue = "")
    val sound_file:String = "",

    @ColumnInfo(name = "isPrayed", defaultValue = "0")
    val isPrayed:Boolean = false,

)
