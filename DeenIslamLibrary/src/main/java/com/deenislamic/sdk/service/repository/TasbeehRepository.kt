package com.deenislamic.sdk.service.repository

import android.util.Log
import com.deenislamic.sdk.service.database.dao.TasbeehDao
import com.deenislamic.sdk.service.database.dao.UserPrefDao
import com.deenislamic.sdk.service.database.entity.Tasbeeh
import com.deenislamic.sdk.service.database.entity.UserPref
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class TasbeehRepository(
    private val tasbeehDao: TasbeehDao?,
    private val userPrefDao: UserPrefDao?
) {

    suspend fun getDuaData(duaid:Int,date:String) =
    withContext(Dispatchers.IO){

        val duaList:ArrayList<Tasbeeh> = arrayListOf(
            Tasbeeh(duaid=1,dua="Subhan Allah", dua_bn = "সুবহান আল্লাহ", dua_arb = "سُبْحَانَ ٱللَّ", audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Subhanallah.mp3"),
            Tasbeeh(duaid=2,dua="Alhamdulillah", dua_bn = "আলহামদুলিল্লাহ", dua_arb = "ٱلْحَمْدُ لِلَّهِ", audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Alhamdulihhah.mp3"),
            Tasbeeh(duaid=3,dua="Bismillah", dua_bn = "বিসমিল্লাহ", dua_arb = "بِسْمِ اللهِ", audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Bismillah.mp3"),
            Tasbeeh(duaid=4,dua="Allahu Akbar", dua_bn = "আল্লাহু আকবার", dua_arb = "الله أكبر",audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Allahuakbar.mp3"),
            Tasbeeh(duaid=5,dua="Astagfirullah", dua_bn = "আস্তাগফিরুল্লাহ", dua_arb = "أَسْتَغْفِرُ اللّٰهَ", audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Astagfirullah.mp3"),
            Tasbeeh(duaid=6,dua="Allah", dua_bn = "আল্লাহ", dua_arb = "الله",audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Allahu.mp3"),
            Tasbeeh(duaid=7,dua="La Ilaha illa Allah", dua_bn = "লা ইলাহা ইল্লাল্লাহ", dua_arb = "لَا إِلَٰهَ إِلَّا ٱللَّ", audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Lailaha_Illalahu.mp3"),
            Tasbeeh(duaid=8,dua="Subhanallahi Wa-Bihamdihi", dua_bn = "সুবহানাল্লাহি ওয়া-বিহামদিহি", dua_arb = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", audioUrl = BASE_CONTENT_URL_SGP +"Content/Audios/Tasbeeh/Subhanallahi_Wa_Bihamdihi.mp3")
        )


        val tasbeehData = tasbeehDao?.select(duaList[duaid].duaid,date)

        Log.e("tasbeehData", Gson().toJson(tasbeehData)+duaid)


        if(tasbeehData?.isEmpty() == true)
        {
            tasbeehDao?.insert(Tasbeeh(
                dua = duaList[duaid].dua,
                dua_bn = duaList[duaid].dua_bn,
                dua_arb = duaList[duaid].dua_arb,
                date = date,
                duaid = duaList[duaid].duaid,
                timestamp = System.currentTimeMillis()
            ))

            duaList[duaid]
        }
        else
            tasbeehData?.get(0)
    }


    suspend fun getUserPref(): UserPref? = withContext(Dispatchers.IO) {
        val userPrefs = userPrefDao?.select()
        if (userPrefs?.isNotEmpty() == true) {
            userPrefs[0]
        } else {
            null // Or handle appropriately
        }
    }

    suspend fun updateTasbeehSound() =
        withContext(Dispatchers.IO)
        {
            val data = userPrefDao?.select()?.get(0)

            val tasbeehSound = data?.tasbeeh_sound !=true

            data?.tasbeeh_sound = tasbeehSound

            val result = data?.let { userPrefDao?.update(listOf(it)) } ?: 0

            if(result>0)
                data
            else
            {
                data?.tasbeeh_sound = !tasbeehSound
                data

            }

        }

    suspend fun setTasbeehCount(target:Int,data:Tasbeeh,todayDate:String) =
        withContext(Dispatchers.IO)
        {
            Log.e("setTasbeehCount",todayDate+Gson().toJson(data))
            var dailyCount = data.daily_count
            val totalCount = data.dua_count+1

            if(data.date == todayDate)
                dailyCount++
            else
                dailyCount = 1

            when(target)
            {
                1->
                {
                    if(data.track1 == 33)
                    tasbeehDao?.update_track1(data.duaid,1,dailyCount,totalCount,todayDate,System.currentTimeMillis())
                    else
                        tasbeehDao?.update_track1(data.duaid,data.track1+1,dailyCount,totalCount,todayDate,System.currentTimeMillis())

                    tasbeehDao?.select(data.duaid,todayDate)

                }

                2->
                {
                    if(data.track2 == 34)
                        tasbeehDao?.update_track2(data.duaid,1,dailyCount,totalCount,todayDate,System.currentTimeMillis())
                    else
                        tasbeehDao?.update_track2(data.duaid,data.track2+1,dailyCount,totalCount,todayDate,System.currentTimeMillis())

                    tasbeehDao?.select(data.duaid,todayDate)
                }

                3->
                {
                    if(data.track3 == 99)
                        tasbeehDao?.update_track3(data.duaid,1,dailyCount,totalCount,todayDate,System.currentTimeMillis())
                    else
                        tasbeehDao?.update_track3(data.duaid,data.track3+1,dailyCount,totalCount,todayDate,System.currentTimeMillis())

                    tasbeehDao?.select(data.duaid,todayDate)
                }

                4->
                {
                    tasbeehDao?.update(data.duaid,totalCount,dailyCount,todayDate,System.currentTimeMillis())
                    tasbeehDao?.select(data.duaid,todayDate)
                }
                else -> tasbeehDao?.select(data.duaid,todayDate)
            }
        }

    suspend fun resetTasbeehCount(type:Int,duaid: Int,date: String) =
        withContext(Dispatchers.IO)
        {
            when(type)
            {
                1->{
                    tasbeehDao?.reset_total(duaid)
                    tasbeehDao?.select(duaid,date)
                }
                2->{
                    tasbeehDao?.reset_today(duaid)
                    tasbeehDao?.select(duaid,date)
                }
                else -> tasbeehDao?.select(duaid,date)
            }
        }

    suspend fun getRecentCount() =
        withContext(Dispatchers.IO)
        {
            tasbeehDao?.recent_count()
        }

    suspend fun getTodayRecentCount(date:String) =
        withContext(Dispatchers.IO)
        {
            tasbeehDao?.today_recent_count(date)
        }

    suspend fun getWeeklyRecentCount(startdate:String,enddate:String) =
        withContext(Dispatchers.IO)
        {
            tasbeehDao?.weeklyRecentCount(startdate,enddate)
        }

    suspend fun getWeeklyChartData(startdate:String,enddate:String) =
        withContext(Dispatchers.IO)
        {
            tasbeehDao?.weeklyChartData(startdate,enddate)
        }

    suspend fun getTotalCount() =
        withContext(Dispatchers.IO)
        {
            tasbeehDao?.total_count()
        }

    suspend fun getTodayCount(date:String) =
        withContext(Dispatchers.IO)
        {
            tasbeehDao?.today_total_count(date)
        }

    suspend fun getWeeklyCount(startdate:String,enddate:String) =
        withContext(Dispatchers.IO)
        {
            tasbeehDao?.weekly_total_count(startdate,enddate)
        }
}