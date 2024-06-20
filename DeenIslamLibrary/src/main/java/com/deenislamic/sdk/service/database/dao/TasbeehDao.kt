package com.deenislamic.sdk.service.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deenislamic.sdk.service.database.entity.Tasbeeh

@Dao
internal abstract class TasbeehDao: BaseDao<Tasbeeh> {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert_replace(obj: Tasbeeh):Long

    @Query("SELECT * from tasbeeh where id=:duaid")
    abstract fun select(duaid:Int):List<Tasbeeh>

    @Query("SELECT * from tasbeeh")
    abstract fun selectAll():List<Tasbeeh>

    @Query("UPDATE tasbeeh SET dua_count=:count,daily_count=:dailyCount,date=:todayDate,timestamp=:times WHERE id=:duaid")
    abstract fun update(duaid: Int,count:Int,dailyCount:Int,todayDate:String,times:Long):Int

    @Query("UPDATE tasbeeh SET track1=:count,daily_count=:dailyCount,dua_count=:totalCount,date=:todayDate,timestamp=:times WHERE id=:duaid")
    abstract fun update_track1(duaid: Int,count:Int,dailyCount:Int,totalCount:Int,todayDate:String,times:Long):Int

    @Query("UPDATE tasbeeh SET track2=:count,daily_count=:dailyCount,dua_count=:totalCount,date=:todayDate,timestamp=:times WHERE id=:duaid")
    abstract fun update_track2(duaid: Int,count:Int,dailyCount:Int,totalCount:Int,todayDate:String,times:Long):Int

    @Query("UPDATE tasbeeh SET track3=:count,daily_count=:dailyCount,dua_count=:totalCount,date=:todayDate,timestamp=:times WHERE id=:duaid")
    abstract fun update_track3(duaid: Int,count:Int,dailyCount:Int,totalCount:Int,todayDate:String,times:Long):Int

    @Query("UPDATE tasbeeh SET dua_count=0,track1=0,track2=0,track3=0,timestamp=0 WHERE id=:duaid")
    abstract fun reset_total(duaid: Int):Int

    @Query("UPDATE tasbeeh SET daily_count=0,track1=0,track2=0,track3=0 WHERE id=:duaid")
    abstract fun reset_today(duaid: Int):Int

    @Query("SELECT * from tasbeeh ORDER BY timestamp DESC")
    abstract fun recent_count():List<Tasbeeh>

}