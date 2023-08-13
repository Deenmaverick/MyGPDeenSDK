package com.deenislam.sdk.service.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import com.deenislam.sdk.Deen
import com.deenislam.sdk.service.database.dao.PrayerNotificationDao
import com.deenislam.sdk.service.database.dao.PrayerTimesDao
import com.deenislam.sdk.service.database.entity.PrayerNotification
import com.deenislam.sdk.service.database.entity.PrayerTimes
import com.deenislam.sdk.service.libs.notification.AlarmReceiver
import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.DeenService
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.utils.RequestBodyMediaType
import com.deenislam.sdk.utils.TimeDiffForPrayer
import com.deenislam.sdk.utils.getPrayerTimeTagWise
import com.deenislam.sdk.utils.toRequestBody
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

internal class PrayerTimesRepository(
    private val deenService: DeenService?,
    private val prayerNotificationDao: PrayerNotificationDao?,
    private val prayerTimesDao: PrayerTimesDao?
):ApiCall {

    suspend fun getPrayerTimes(localtion:String,language:String,requiredDate:String) = makeApicall {

        /*val prayerTimeData = prayerTimesDao.select(requiredDate)

        if(prayerTimeData.isNotEmpty())
            PrayerTimesResponse(Data = prayerTimeData[0], Message = "Success", Success = true, TotalData = 1)

        else {*/
        val body = JSONObject()
        body.put("location", localtion)
        body.put("language", language)
        body.put("requiredDate", requiredDate)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.prayerTime(parm = requestBody)
        //}
    }

    suspend fun getNotificationData(date:String,prayer_tag:String) =
        withContext(Dispatchers.IO)
        {
            try {

                var prayerNotificationData = prayerNotificationDao?.select(date,prayer_tag)

                if(prayerNotificationData?.isNotEmpty() == true)
                    prayerNotificationData[0]
                else
                {
                    prayerNotificationDao?.insert(PrayerNotification(date = date, prayer_tag = prayer_tag, id = 0))
                    prayerNotificationData = prayerNotificationDao?.select(date,prayer_tag)

                    if(prayerNotificationData?.isNotEmpty() == true)
                        prayerNotificationData[0]
                    else null
                }

            }
            catch (e:Exception)
            {
                null
            }
        }

    suspend fun updatePrayerNotification(
        date: String,
        prayer_tag: String,
        state: Int = 0,
        sound_file: String = "",
        prayerTimesResponse: PrayerTimesResponse?
    ) =
        withContext(Dispatchers.IO)
        {
            try {

                getNotificationData(date = date, prayer_tag = prayer_tag)

                if(prayerNotificationDao?.update(date,prayer_tag,state) !=0) {
                    val getNotificationData = prayerNotificationDao?.select(date,prayer_tag)
                    if(getNotificationData?.isNotEmpty() == true)
                    {
                        val pid = getNotificationData[0]?.id?:0
                        if(prayerTimesResponse!=null) {
                            val notifyTime = getPrayerTimeTagWise(prayer_tag, date, prayerTimesResponse)

                            Log.e("PRAYER_NT",notifyTime.TimeDiffForPrayer()+""+date+Gson().toJson(prayerTimesResponse))

                            if(pid <= 0)
                                return@withContext 0

                            else if (notifyTime > 0L) {
                                setNotification(SystemClock.elapsedRealtime() + notifyTime, pid)
                                return@withContext  1
                            }
                            else
                                return@withContext 2
                        } else 0
                    }
                    else 0
                }
                else
                    0
            }
            catch (e:Exception)
            {
                0
            }
        }


    private fun setNotification(time:Long,reqcode:Int)
    {
        val notifyIntent = Intent(Deen.appContext, AlarmReceiver::class.java)
        notifyIntent.putExtra("pid",reqcode)

        val notifyPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                Deen.appContext,
                reqcode,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

        val alarmManager = Deen.appContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            time,
            notifyPendingIntent
        )
    }



    suspend fun chachePrayerTimes(date:String, data: PrayerTimes):Boolean =
        withContext(Dispatchers.IO)
        {
            val prayerTimeData = prayerTimesDao?.select(date)
            if(prayerTimeData?.isEmpty() == true)
            {
                try {

                    prayerTimesDao?.insert(PrayerTimes(
                        Date = date,
                        Day = data.Day,
                        IslamicDate = data.IslamicDate,
                        Sehri = data.Sehri,
                        Fajr = data.Fajr,
                        Sunrise = data.Sunrise,
                        Ishrak = data.Ishrak,
                        Noon = data.Noon,
                        Juhr = data.Juhr,
                        Asr = data.Asr,
                        Magrib = data.Magrib,
                        Isha = data.Isha,
                        Tahajjut = data.Tahajjut
                    ))

                    true

                }catch (e:java.lang.Exception)
                {
                    false
                }
            }
            else
                true
        }

    suspend fun getDateWiseNotificationData(date:String) =
        withContext(Dispatchers.IO)
        {
            prayerNotificationDao?.select(date)
        }

    suspend fun setPrayerTrack(date:String,prayer_tag: String,bol:Boolean) =
        withContext(Dispatchers.IO)
        {
            getNotificationData(date = date,prayer_tag = prayer_tag)
            prayerNotificationDao?.update(pdate =date,prayer_tag=prayer_tag,bol=bol)
            getDateWiseNotificationData(date)
        }


    suspend fun updatePrayerTrackAuto(date:String,prayer_tag: String) =
        withContext(Dispatchers.IO)
        {
            val data = getNotificationData(date = date,prayer_tag = prayer_tag)
            prayerNotificationDao?.update(pdate =date,prayer_tag=prayer_tag,bol= data?.isPrayed != true)
            getDateWiseNotificationData(date)
        }

    suspend fun setPrayerTimeTrack(language: String,prayer_tag: String,isPrayed:Boolean) = makeApicall {
        val body = JSONObject()
        body.put("language", language)
        body.put(prayer_tag.lowercase(), isPrayed)

        Log.e("setPrayerTimeTrack",prayer_tag+isPrayed)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.setPrayerTimeTrack(parm = requestBody)
    }


    suspend fun getPrayerTimeTrack() = makeApicall {
        deenService?.getPrayerTimeTrack()
    }


    suspend fun clearPrayerNotification(
        date: String
    ):Int =
        withContext(Dispatchers.IO)
        {
            try {

                val prayerNotificationData = prayerNotificationDao?.select(date)

                if(prayerNotificationData?.isNotEmpty() == true)
                    return@withContext prayerNotificationDao?.clearAllNotification(date,1)?:0

                else
                    1

                }
            catch (e:Exception)
            {
                0
            }
        }

    suspend fun enableAllPrayerNotification(
        date: String,
        prayer_tag: String,
        state: Int = 0,
        sound_file: String = "",
        prayerTimesResponse: PrayerTimesResponse?,
        isChecking:Boolean = true
    ) =
        withContext(Dispatchers.IO)
        {
            try {

                if(prayerNotificationDao?.update(date,prayer_tag,state) !=0) {
                    val getNotificationData = prayerNotificationDao?.select(date,prayer_tag)
                    if(getNotificationData?.isNotEmpty() == true)
                    {
                        val pid = getNotificationData[0]?.id?:0
                        if(prayerTimesResponse!=null) {
                            val notifyTime = getPrayerTimeTagWise(prayer_tag, date, prayerTimesResponse)

                            Log.e("PRAYER_NT",notifyTime.TimeDiffForPrayer()+""+date+Gson().toJson(prayerTimesResponse))

                            if(pid <= 0)
                                return@withContext 0

                            else if (notifyTime > 0L) {
                                setNotification(SystemClock.elapsedRealtime() + notifyTime, pid)
                                return@withContext  1
                            }
                            else
                                return@withContext 2
                        } else 0
                    }
                    else 0
                }
                else {
                    if(isChecking) {
                        getNotificationData(date = date, prayer_tag = prayer_tag)

                      /*  val test = enableAllPrayerNotification(
                            date = date,
                            prayer_tag = prayer_tag,
                            state = state,
                            sound_file = sound_file,
                            prayerTimesResponse = prayerTimesResponse,
                            isChecking = false)*/


                    }
                    else
                        0
                }
            }
            catch (e:Exception)
            {
                0
            }
        }
}