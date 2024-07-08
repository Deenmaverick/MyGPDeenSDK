package com.deenislamic.sdk.service.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.deenislamic.sdk.service.database.entity.Tasbeeh
import com.deenislamic.sdk.service.models.tasbeeh.WeeklyChartData

@Dao
abstract class TasbeehDao:BaseDao<Tasbeeh> {

    @Query("SELECT * from tasbeeh where duaid=:getduaid and date=:todaydate")
    abstract fun select(getduaid:Int,todaydate:String):List<Tasbeeh>

    @Query("SELECT * from tasbeeh")
    abstract fun selectAll():List<Tasbeeh>

    @Query("UPDATE tasbeeh SET dua_count=:count,daily_count=:dailyCount,timestamp=:times WHERE duaid=:getduaid and date=:todayDate")
    abstract fun update(getduaid: Int,count:Int,dailyCount:Int,todayDate:String,times:Long):Int

    @Query("UPDATE tasbeeh SET track1=:count,daily_count=:dailyCount,dua_count=:totalCount,timestamp=:times WHERE duaid=:getduaid and date=:todayDate")
    abstract fun update_track1(getduaid: Int,count:Int,dailyCount:Int,totalCount:Int,todayDate:String,times:Long):Int

    @Query("UPDATE tasbeeh SET track2=:count,daily_count=:dailyCount,dua_count=:totalCount,timestamp=:times WHERE duaid=:getduaid and date=:todayDate")
    abstract fun update_track2(getduaid: Int,count:Int,dailyCount:Int,totalCount:Int,todayDate:String,times:Long):Int

    @Query("UPDATE tasbeeh SET track3=:count,daily_count=:dailyCount,dua_count=:totalCount,timestamp=:times WHERE duaid=:getduaid and date=:todayDate")
    abstract fun update_track3(getduaid: Int,count:Int,dailyCount:Int,totalCount:Int,todayDate:String,times:Long):Int

    @Query("UPDATE tasbeeh SET dua_count=0,track1=0,track2=0,track3=0,timestamp=0 WHERE duaid=:getduaid")
    abstract fun reset_total(getduaid: Int):Int

    @Query("UPDATE tasbeeh SET daily_count=0,track1=0,track2=0,track3=0 WHERE duaid=:getduaid")
    abstract fun reset_today(getduaid: Int):Int

    @Query("SELECT * from tasbeeh WHERE  duaid>0 ORDER BY timestamp DESC")
    abstract fun recent_count():List<Tasbeeh>

    @Query("SELECT * from tasbeeh where date=:todayDate AND dua_count>0 AND duaid>0 ORDER BY timestamp DESC")
    abstract fun today_recent_count(todayDate:String):List<Tasbeeh>

  /*  @Query("SELECT * from tasbeeh where date BETWEEN :startOfWeek AND :endOfWeek AND dua_count>0 ORDER BY timestamp DESC")
    abstract fun weekly_recent_count(startOfWeek: String, endOfWeek: String):List<Tasbeeh>
*/
  @Query("SELECT id, duaid, dua, SUM(dua_count) as dua_count, " +
          "dua_bn, dua_arb, track1, track2, track3, daily_count, date, timestamp, audioUrl " +
          "FROM tasbeeh " +
          "WHERE date BETWEEN :startOfWeek AND :endOfWeek AND duaid > 0 " +
          "GROUP BY duaid, dua " + // Removed TRIM(dua)
          "ORDER BY timestamp DESC")
  abstract fun weeklyRecentCount(startOfWeek: String, endOfWeek: String): List<Tasbeeh>

    @Query("SELECT date, SUM(dua_count) as totalDuaCount " +
            "FROM tasbeeh " +
            "WHERE date BETWEEN :startOfWeek AND :endOfWeek AND duaid > 0 " +
            "GROUP BY date " +
            "ORDER BY date DESC")
    abstract fun weeklyChartData(startOfWeek: String, endOfWeek: String): List<WeeklyChartData>


    @Query("SELECT SUM(dua_count) from tasbeeh where date=:todayDate AND duaid>0")
    abstract fun today_total_count(todayDate:String): Long

    @Query("SELECT SUM(dua_count) from tasbeeh WHERE date BETWEEN :startOfWeek AND :endOfWeek AND duaid>0")
    abstract fun weekly_total_count(startOfWeek: String, endOfWeek: String): Long

    @Query("SELECT SUM(dua_count) from tasbeeh WHERE  duaid>0")
    abstract fun total_count(): Long
}