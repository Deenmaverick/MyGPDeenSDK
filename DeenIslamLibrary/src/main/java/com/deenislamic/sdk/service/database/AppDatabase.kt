package com.deenislamic.sdk.service.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.deenislamic.sdk.service.database.dao.*
import com.deenislamic.sdk.service.database.entity.*

@Database(
    entities = [
        UserPref::class,
        FavoriteMenu::class,
        PrayerNotification::class,
        PrayerTimes::class,
        Tasbeeh::class,
        PlayerSettingPref::class
               ],
    version = 2,
    /*autoMigrations = [
        AutoMigration(from = 1, to = 8),
                     ],*/
    exportSchema = true

)
@TypeConverters(Converters::class)
internal abstract class AppDatabase : RoomDatabase() {

    abstract fun UserPrefDAO(): UserPrefDao
    abstract fun FavMenuDAO():FavMenuDao
    abstract fun PrayerNotificationDAO():PrayerNotificationDao
    abstract fun PrayerTimesDAO():PrayerTimesDao
    abstract fun TasbeehDao():TasbeehDao
    abstract fun PlayerSettingDao():PlayerSettingDao
}