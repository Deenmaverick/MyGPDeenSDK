package com.deenislam.sdk.service.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.deenislam.sdk.service.database.entity.PrayerNotification

@Dao
internal abstract class PrayerNotificationDao: BaseDao<PrayerNotification> {

    @Query("SELECT * from prayer_notification where date=:pdate and prayer_tag=:prayer_tag")
    abstract fun select(pdate:String,prayer_tag:String):List<PrayerNotification?>

    @Query("UPDATE  prayer_notification SET state=:state where prayer_tag=:prayer_tag and date=:pdate")
    abstract fun update(pdate:String,prayer_tag:String,state:Int):Int

    @Query("SELECT * from prayer_notification where date=:pdate")
    abstract fun select(pdate:String):List<PrayerNotification>

    @Query("SELECT * from prayer_notification where id=:pid")
    abstract fun select(pid:Int):List<PrayerNotification>

    @Query("UPDATE  prayer_notification SET isPrayed=:bol where prayer_tag=:prayer_tag and date=:pdate")
    abstract fun update(pdate:String,prayer_tag:String,bol:Boolean):Int

    @Query("UPDATE  prayer_notification SET state=:state")
    abstract fun clearAllNotification(state:Int):Int

    @Query("UPDATE  prayer_notification SET state=1 where id=:pid")
    abstract fun clearNotificationByID(pid:Int):Int

    @Query("SELECT * from prayer_notification where state=3 or state=2")
    abstract fun select_all_activate_notification():List<PrayerNotification>

    @Query("SELECT * from prayer_notification where id=:pid and (state=3 or state=2)")
    abstract fun select_active_notification(pid:Int):List<PrayerNotification>



}