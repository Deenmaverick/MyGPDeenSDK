package com.deenislam.sdk.service.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.deenislam.sdk.service.database.entity.PrayerTimes

@Dao
internal abstract class PrayerTimesDao: BaseDao<PrayerTimes> {

    @Query("SELECT * from prayer_times where Date=:date")
    abstract fun select(date:String):List<PrayerTimes>

}