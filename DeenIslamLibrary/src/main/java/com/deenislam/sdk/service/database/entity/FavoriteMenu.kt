package com.deenislam.sdk.service.database.entity

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fav_menu")
data class FavoriteMenu(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id:Int,

    @ColumnInfo(name = "icon")
    var icon:Int,
    @ColumnInfo(name = "name")
    var name:String,
    @ColumnInfo(name = "menutag")
    var menutag:String

)
