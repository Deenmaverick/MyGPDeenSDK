package com.deenislam.sdk.service.repository

import com.deenislam.sdk.service.database.dao.TasbeehDao
import com.deenislam.sdk.service.database.dao.UserPrefDao
import com.deenislam.sdk.service.database.entity.Tasbeeh
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TasbeehRepository(
    private val tasbeehDao: TasbeehDao?,
    private val userPrefDao: UserPrefDao?
) {

    suspend fun getDuaData(duaid:Int) =
    withContext(Dispatchers.IO){

         val duaList:ArrayList<Tasbeeh> = arrayListOf(
             Tasbeeh(id=0,dua="", dua_bn = "", dua_arb = ""),
             Tasbeeh(id=1,dua="Subhan Allah", dua_bn = "সুবহান আল্লাহ", dua_arb = "سُبْحَانَ ٱللَّ"),
             Tasbeeh(id=2,dua="Alhamdulillah", dua_bn = "আলহামদুলিল্লাহ", dua_arb = "ٱلْحَمْدُ لِلَّهِ"),
             Tasbeeh(id=3,dua="Bismillah", dua_bn = "বিসমিল্লাহ", dua_arb = "بِسْمِ اللهِ"),
             Tasbeeh(id=4,dua="Allahu Akbar", dua_bn = "আল্লাহু আকবার", dua_arb = "الله أكبر"),
             Tasbeeh(id=5,dua="Astagfirullah", dua_bn = "আস্তাগফিরুল্লাহ", dua_arb = "أَسْتَغْفِرُ اللّٰهَ"),
             Tasbeeh(id=6,dua="Allah", dua_bn = "আল্লাহ", dua_arb = "الله"),
             Tasbeeh(id=7,dua="La Ilaha illa Allah", dua_bn = "লা ইলাহা ইল্লাল্লাহ", dua_arb = "لَا إِلَٰهَ إِلَّا ٱللَّ"),
             Tasbeeh(id=8,dua="Subhanallahi Wa-Bihamdihi", dua_bn = "সুবহানাল্লাহি ওয়া-বিহামদিহি", dua_arb = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ")
         )

        val tasbeehData = tasbeehDao?.select(duaid)

        if(tasbeehData?.isEmpty() == true)
        {
            tasbeehDao?.insert(Tasbeeh(
                id = duaList[duaid].id,
                dua = duaList[duaid].dua,
                dua_bn = duaList[duaid].dua_bn,
                dua_arb = duaList[duaid].dua_arb,
                timestamp = System.currentTimeMillis()
            ))

            duaList[duaid]
        }
        else
            tasbeehData?.get(0)
    }


    suspend fun getUserPref() =
        withContext(Dispatchers.IO)
        {
            userPrefDao?.select()?.get(0)
        }

    suspend fun updateTasbeehSound() =
        withContext(Dispatchers.IO)
        {
            val data = userPrefDao?.select()?.get(0)

            val tasbeehSound = data?.tasbeeh_sound !=true

            data?.tasbeeh_sound = tasbeehSound

            if(userPrefDao?.update(listOf(data))!=0)
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
                    tasbeehDao?.update_track1(data.id,1,dailyCount,totalCount,todayDate,System.currentTimeMillis())
                    else
                        tasbeehDao?.update_track1(data.id,data.track1+1,dailyCount,totalCount,todayDate,System.currentTimeMillis())

                    tasbeehDao?.select(data.id)
                }

                2->
                {
                    if(data.track2 == 34)
                        tasbeehDao?.update_track2(data.id,1,dailyCount,totalCount,todayDate,System.currentTimeMillis())
                    else
                        tasbeehDao?.update_track2(data.id,data.track2+1,dailyCount,totalCount,todayDate,System.currentTimeMillis())

                    tasbeehDao?.select(data.id)
                }

                3->
                {
                    if(data.track3 == 99)
                        tasbeehDao?.update_track3(data.id,1,dailyCount,totalCount,todayDate,System.currentTimeMillis())
                    else
                        tasbeehDao?.update_track3(data.id,data.track3+1,dailyCount,totalCount,todayDate,System.currentTimeMillis())

                    tasbeehDao?.select(data.id)
                }

                4->
                {
                    tasbeehDao?.update(data.id,totalCount,dailyCount,todayDate,System.currentTimeMillis())
                    tasbeehDao?.select(data.id)
                }
                else -> tasbeehDao?.select(data.id)
            }
        }

    suspend fun resetTasbeehCount(type:Int,duaid: Int) =
        withContext(Dispatchers.IO)
        {
            when(type)
            {
                1->{
                    tasbeehDao?.reset_total(duaid)
                    tasbeehDao?.select(duaid)
                }
                2->{
                    tasbeehDao?.reset_today(duaid)
                    tasbeehDao?.select(duaid)
                }
                else -> tasbeehDao?.select(duaid)
            }
        }

    suspend fun getRecentCount() =
        withContext(Dispatchers.IO)
        {
            tasbeehDao?.recent_count()
        }
}