package com.deenislam.sdk.service.di;

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.deenislam.sdk.Deen
import com.deenislam.sdk.service.database.AppDatabase
import com.deenislam.sdk.service.database.dao.PrayerNotificationDao
import com.deenislam.sdk.service.database.dao.PrayerTimesDao
import com.deenislam.sdk.service.database.dao.TasbeehDao
import com.deenislam.sdk.service.database.dao.UserPrefDao
import com.deenislam.sdk.utils.tryCatch

internal class DatabaseProvider {

    private var databse:AppDatabase ? =null
    companion object {
        var instance: DatabaseProvider? = null
    }

    val MIGRATION_TEST: Migration = object : Migration(14, 15) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Since we didn't alter the table, there's nothing else to do here.

            database.execSQL("CREATE TABLE IF NOT EXISTS `userpref` (" +
                    "`id` INTEGER NOT NULL DEFAULT 1," +
                    " `language` TEXT NOT NULL DEFAULT 'en'," +
                    " `location_setting` INTEGER NOT NULL DEFAULT 0," +
                    " `token` TEXT DEFAULT ''," +
                    "`username` TEXT DEFAULT ''," +
                    "`refresh_token` TEXT DEFAULT ''," +
                    "`location_city` TEXT DEFAULT ''," +
                    "`location_country` TEXT DEFAULT ''," +
                    "`tasbeeh_sound` INTEGER NOT NULL DEFAULT 1,"+
                    " PRIMARY KEY(`id`))")
            verify_all_userpref_col(database)
            database.execSQL("CREATE TABLE IF NOT EXISTS `userpref_new` (" +
                    "`id` INTEGER NOT NULL DEFAULT 1," +
                    " `language` TEXT NOT NULL DEFAULT 'en'," +
                    " `location_setting` INTEGER NOT NULL DEFAULT 0," +
                    " `token` TEXT DEFAULT ''," +
                    "`username` TEXT DEFAULT ''," +
                    "`refresh_token` TEXT DEFAULT ''," +
                    "`location_city` TEXT DEFAULT ''," +
                    "`location_country` TEXT DEFAULT ''," +
                    "`tasbeeh_sound` INTEGER NOT NULL DEFAULT 1,"+
                    " PRIMARY KEY(`id`))")
            database.execSQL("INSERT OR IGNORE INTO userpref_new SELECT * FROM userpref")
            database.execSQL("DROP TABLE userpref")
            database.execSQL("ALTER TABLE userpref_new RENAME TO userpref")


            database.execSQL("CREATE TABLE IF NOT EXISTS `fav_menu` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `icon` INTEGER NOT NULL, `name` TEXT NOT NULL, `menutag` TEXT NOT NULL)")
            database.execSQL("CREATE TABLE IF NOT EXISTS `fav_menu_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `icon` INTEGER NOT NULL, `name` TEXT NOT NULL, `menutag` TEXT NOT NULL)")
            database.execSQL("INSERT OR IGNORE INTO fav_menu_new SELECT * FROM fav_menu")
            database.execSQL("DROP TABLE fav_menu")
            database.execSQL("ALTER TABLE fav_menu_new RENAME TO fav_menu")


            database.execSQL("CREATE TABLE IF NOT EXISTS `prayer_notification` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT 0,`date` TEXT NOT NULL DEFAULT '', `prayer_tag` TEXT NOT NULL DEFAULT '', `state` INTEGER NOT NULL DEFAULT 1, `sound_file` TEXT NOT NULL DEFAULT '',`isPrayed` INTEGER NOT NULL DEFAULT 0)")
            verify_all_prayer_notification_col(database)
            database.execSQL("CREATE TABLE IF NOT EXISTS `prayer_notification_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT 0,`date` TEXT NOT NULL DEFAULT '', `prayer_tag` TEXT NOT NULL DEFAULT '', `state` INTEGER NOT NULL DEFAULT 1, `sound_file` TEXT NOT NULL DEFAULT '',`isPrayed` INTEGER NOT NULL DEFAULT 0)")
            database.execSQL("INSERT OR IGNORE INTO prayer_notification_new SELECT * FROM prayer_notification")
            database.execSQL("DROP TABLE prayer_notification")
            database.execSQL("ALTER TABLE prayer_notification_new RENAME TO prayer_notification")


            database.execSQL("CREATE TABLE IF NOT EXISTS `tasbeeh` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT 0, `dua` TEXT NOT NULL DEFAULT '', `dua_bn` TEXT NOT NULL DEFAULT '', `track1` INTEGER NOT NULL DEFAULT 0, `track2` INTEGER NOT NULL DEFAULT 0, `track3` INTEGER NOT NULL DEFAULT 0, `dua_count` INTEGER NOT NULL DEFAULT 0, `daily_count` INTEGER NOT NULL DEFAULT 0, `date` TEXT NOT NULL DEFAULT '', `timestamp` INTEGER NOT NULL DEFAULT 0)")
            verify_all_tasbeeh_col(database)
            database.execSQL("CREATE TABLE IF NOT EXISTS `tasbeeh_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT 0, `dua` TEXT NOT NULL DEFAULT '', `dua_bn` TEXT NOT NULL DEFAULT '', `dua_arb` TEXT NOT NULL DEFAULT '',`track1` INTEGER NOT NULL DEFAULT 0, `track2` INTEGER NOT NULL DEFAULT 0, `track3` INTEGER NOT NULL DEFAULT 0, `dua_count` INTEGER NOT NULL DEFAULT 0, `daily_count` INTEGER NOT NULL DEFAULT 0, `date` TEXT NOT NULL DEFAULT '',`timestamp` INTEGER NOT NULL DEFAULT 0)")
            database.execSQL("INSERT OR IGNORE INTO tasbeeh_new SELECT * FROM tasbeeh")
            database.execSQL("DROP TABLE tasbeeh")
            database.execSQL("ALTER TABLE tasbeeh_new RENAME TO tasbeeh")


            /*  database.execSQL("CREATE TABLE IF NOT EXISTS `prayer_times` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT 0, `Asr` TEXT NOT NULL DEFAULT '', `Date` TEXT NOT NULL DEFAULT '',`Day` INTEGER NOT NULL DEFAULT 0, `Fajr` TEXT NOT NULL DEFAULT '', `Isha` TEXT NOT NULL DEFAULT '', `Ishrak` TEXT NOT NULL DEFAULT '', `IslamicDate` TEXT NOT NULL DEFAULT '', `Juhr` TEXT NOT NULL DEFAULT '', `Magrib` TEXT NOT NULL DEFAULT '', `Noon` TEXT NOT NULL DEFAULT '', `Sehri` TEXT NOT NULL DEFAULT '', `Sunrise` TEXT NOT NULL DEFAULT '', `Tahajjut` TEXT NOT NULL DEFAULT '')")
              verify_all_prayer_times_col(database)
              database.execSQL("CREATE TABLE IF NOT EXISTS `prayer_times_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT 0, `Asr` TEXT NOT NULL DEFAULT '', `Date` TEXT NOT NULL DEFAULT '',`Day` INTEGER NOT NULL DEFAULT 0, `Fajr` TEXT NOT NULL DEFAULT '', `Isha` TEXT NOT NULL DEFAULT '', `Ishrak` TEXT NOT NULL DEFAULT '', `IslamicDate` TEXT NOT NULL DEFAULT '', `Juhr` TEXT NOT NULL DEFAULT '', `Magrib` TEXT NOT NULL DEFAULT '', `Noon` TEXT NOT NULL DEFAULT '', `Sehri` TEXT NOT NULL DEFAULT '', `Sunrise` TEXT NOT NULL DEFAULT '', `Tahajjut` TEXT NOT NULL DEFAULT '')")
              database.execSQL("INSERT OR IGNORE INTO prayer_times_new SELECT * FROM prayer_times")
              database.execSQL("DROP TABLE prayer_times")
              database.execSQL("ALTER TABLE prayer_times_new RENAME TO prayer_times")
  */

        }
    }

    private fun verify_all_tasbeeh_col(database: SupportSQLiteDatabase)
    {
        tryCatch { database.execSQL("ALTER TABLE  `tasbeeh` ADD `daily_count` INTEGER NOT NULL DEFAULT 0") }
        tryCatch { database.execSQL("ALTER TABLE  `tasbeeh` ADD `date` TEXT NOT NULL DEFAULT ''") }
        tryCatch { database.execSQL("ALTER TABLE  `tasbeeh` ADD `track1` INTEGER NOT NULL DEFAULT 0") }
        tryCatch { database.execSQL("ALTER TABLE  `tasbeeh` ADD `track2` INTEGER NOT NULL DEFAULT 0") }
        tryCatch { database.execSQL("ALTER TABLE  `tasbeeh` ADD `track3` INTEGER NOT NULL DEFAULT 0") }
    }

    private fun verify_all_prayer_notification_col(database: SupportSQLiteDatabase)
    {
        tryCatch { database.execSQL("ALTER TABLE  `prayer_notification` ADD `date` TEXT NOT NULL DEFAULT ''") }
        tryCatch { database.execSQL("ALTER TABLE  `prayer_notification` ADD `isPrayed` INTEGER NOT NULL DEFAULT 0") }
    }

    private fun verify_all_userpref_col(database: SupportSQLiteDatabase)
    {
        tryCatch { database.execSQL("ALTER TABLE `userpref` ADD `tasbeeh_sound` INTEGER NOT NULL DEFAULT 1") }
    }

    fun getInstance(): DatabaseProvider {
        if (instance == null)
            instance = DatabaseProvider()

        return instance as DatabaseProvider
    }

    private fun provideAppDatabase() {

        if (instance?.databse == null)
            instance?.databse =
        Room.databaseBuilder(
            Deen.appContext!!,
            AppDatabase::class.java, "deenislam.db"
        )
            .addMigrations(MIGRATION_TEST)
            //.allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

    }


    fun provideUserPrefDao(): UserPrefDao? {
        provideAppDatabase()
        return instance?.databse?.UserPrefDAO()
    }

    fun providePrayerNotificationDao():PrayerNotificationDao? {
        provideAppDatabase()
        return instance?.databse?.PrayerNotificationDAO()
    }

    fun providePrayerTimesDao(): PrayerTimesDao? {
        provideAppDatabase()
        return instance?.databse?.PrayerTimesDAO()
    }

    fun provideTasbeehDao(): TasbeehDao? {
        provideAppDatabase()
        return instance?.databse?.TasbeehDao()
    }

}

