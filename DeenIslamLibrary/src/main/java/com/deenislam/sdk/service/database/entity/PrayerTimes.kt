package com.deenislam.sdk.service.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_times")
internal data class PrayerTimes(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", defaultValue = "0")
    val id:Int = 0,
    @ColumnInfo(name = "Asr", defaultValue = "")
    val Asr: String,
    @ColumnInfo(name = "Date", defaultValue = "")
    val Date: String,
    @ColumnInfo(name = "Day", defaultValue = "0")
    val Day: Int,
    @ColumnInfo(name = "Fajr", defaultValue = "")
    val Fajr: String,
    @ColumnInfo(name = "Isha", defaultValue = "")
    val Isha: String,
    @ColumnInfo(name = "Ishrak", defaultValue = "")
    val Ishrak: String,
    @ColumnInfo(name = "IslamicDate", defaultValue = "")
    val IslamicDate: String,
    @ColumnInfo(name = "Juhr", defaultValue = "")
    val Juhr: String,
    @ColumnInfo(name = "Magrib", defaultValue = "")
    val Magrib: String,
    @ColumnInfo(name = "Noon", defaultValue = "")
    val Noon: String,
    @ColumnInfo(name = "Sehri", defaultValue = "")
    val Sehri: String,
    @ColumnInfo(name = "Sunrise", defaultValue = "")
    val Sunrise: String,
    @ColumnInfo(name = "Tahajjut", defaultValue = "")
    val Tahajjut: String,


)
