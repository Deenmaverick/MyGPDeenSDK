package com.deenislamic.sdk.service.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasbeeh")
data class Tasbeeh(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", defaultValue = "0")
    val id:Int = 0,

    @ColumnInfo(name = "dua", defaultValue = "")
    val dua: String,

    @ColumnInfo(name = "dua_bn", defaultValue = "")
    val dua_bn: String,

    @ColumnInfo(name = "dua_arb", defaultValue = "")
    val dua_arb: String,

    @ColumnInfo(name = "track1", defaultValue = "")
    val track1: Int = 0,

    @ColumnInfo(name = "track2", defaultValue = "")
    val track2: Int = 0,

    @ColumnInfo(name = "track3", defaultValue = "")
    val track3: Int = 0,

    @ColumnInfo(name = "dua_count", defaultValue = "0")
    val dua_count: Int = 0,

    @ColumnInfo(name = "daily_count", defaultValue = "0")
    val daily_count: Int = 0,

    @ColumnInfo(name = "date", defaultValue = "")
    val date: String = "",

    @ColumnInfo(name = "duaid", defaultValue = "0")
    val duaid: Int = 0,

    @ColumnInfo(name = "timestamp", defaultValue = "0")
    val timestamp: Long = 0,
    @ColumnInfo(name = "audioUrl", defaultValue = "")
    val audioUrl:String = ""

)
