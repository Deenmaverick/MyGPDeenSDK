package com.deenislam.sdk.service.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.deenislam.sdk.service.database.dao.*
import com.deenislam.sdk.service.database.entity.*

@Database(
    entities = [
        UserPref::class,
        FavoriteMenu::class,
        PrayerNotification::class,
        PrayerTimes::class,
        Tasbeeh::class
               ],
    version = 15,
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
}