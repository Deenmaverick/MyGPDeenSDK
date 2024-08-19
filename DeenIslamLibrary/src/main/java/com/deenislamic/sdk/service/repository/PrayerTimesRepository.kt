package com.deenislamic.sdk.service.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import com.deenislamic.sdk.BuildConfig
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.service.database.dao.PrayerNotificationDao
import com.deenislamic.sdk.service.database.dao.PrayerTimesDao
import com.deenislamic.sdk.service.database.entity.PrayerNotification
import com.deenislamic.sdk.service.database.entity.PrayerTimes
import com.deenislamic.sdk.service.libs.notification.AlarmReceiver
import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.network.api.DeenService
import com.deenislamic.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.TimeDiffForPrayer
import com.deenislamic.sdk.utils.getPrayerTimeTagWise
import com.deenislamic.sdk.utils.toRequestBody
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

    suspend fun getPrayerTimeSDK(localtion:String,language:String,requiredDate:String) = makeApicall {

        /*val prayerTimeData = prayerTimesDao.select(requiredDate)

        if(prayerTimeData.isNotEmpty())
            PrayerTimesResponse(Data = prayerTimeData[0], Message = "Success", Success = true, TotalData = 1)

        else {*/
        val body = JSONObject()
        body.put("location", localtion)
        body.put("language", language)
        body.put("requiredDate", requiredDate)
        body.put("client_id", BuildConfig.CLIENT_ID)
        body.put("client_secret", BuildConfig.CLIENT_SECRET)


        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.prayerTimeSDK(parm = requestBody)
        //}
    }

    suspend fun getNotificationData(date:String,prayer_tag:String,finalState:Int = 1) =
        withContext(Dispatchers.IO)
        {
            try {


                var prayerNotificationData = prayerNotificationDao?.select(date,prayer_tag)

                if(prayerNotificationData?.isNotEmpty() == true)
                    prayerNotificationData[0]
                else
                {
                    prayerNotificationDao?.insert(PrayerNotification(date = date, prayer_tag = prayer_tag, state = finalState))
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

    suspend fun getNotificationDataOnly(date:String,prayer_tag:String,finalState:Int = 1) =
        withContext(Dispatchers.IO)
        {
            try {

                val prayerNotificationData = prayerNotificationDao?.select(date,prayer_tag)

                if(prayerNotificationData?.isNotEmpty() == true)
                    prayerNotificationData[0]
                else
                    null

            }
            catch (e:Exception)
            {
                null
            }
        }

    suspend fun addNotificationData(date:String,prayer_tag:String,finalState:Int = 1) =
        withContext(Dispatchers.IO)
        {
            try {


                    prayerNotificationDao?.insert(PrayerNotification(date = date, prayer_tag = prayer_tag, state = finalState))
                    val prayerNotificationData = prayerNotificationDao?.select(date,prayer_tag)

                    if(prayerNotificationData?.isNotEmpty() == true)
                        prayerNotificationData[0]
                    else null

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
        prayerTimesResponse: PrayerTimesResponse?,
        isFromInsideSDK:Boolean = true
    ) =
        withContext(Dispatchers.IO)
        {
            try {


                    val existingPrayerNotifyData =
                        getNotificationData(date = date, prayer_tag = prayer_tag)

                   /* if (existingPrayerNotifyData != null && !isFromInsideSDK && prayer_tag!="Notification") {
                        Log.e("existingPrayerNotify", Gson().toJson(existingPrayerNotifyData))

                        return@withContext 1
                    }
                    else*/
                    //getNotificationData(date = date, prayer_tag = prayer_tag)


                var finalState = state
                if(!isFromInsideSDK && (existingPrayerNotifyData?.state == 2 || existingPrayerNotifyData?.state == 3))
                    finalState = existingPrayerNotifyData.state

                if(prayerNotificationDao?.update(date,prayer_tag,finalState) !=0) {
                    val getNotificationData = prayerNotificationDao?.select(date,prayer_tag)
                    if(getNotificationData?.isNotEmpty() == true)
                    {
                        val notificationData = getNotificationData[0]

                        if(notificationData?.prayer_tag == "Notification" && notificationData.state == finalState)
                            return@withContext 1

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

        //CancleExistingNotification(reqcode)

        val notifyIntent = Intent(DeenSDKCore.appContext, AlarmReceiver::class.java)
        notifyIntent.putExtra("pid",reqcode)

        val notifyPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                DeenSDKCore.appContext,
                reqcode,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

        val alarmManager = DeenSDKCore.appContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            time,
            notifyPendingIntent
        )
    }

    private fun CancleExistingNotification(reqcode: Int)
    {
        val notifyIntent = Intent(DeenSDKCore.appContext, AlarmReceiver::class.java)
        notifyIntent.putExtra("pid",reqcode)

        val notifyPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                DeenSDKCore.appContext,
                reqcode,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

        val alarmManager = DeenSDKCore.appContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(notifyPendingIntent)
        notifyPendingIntent.cancel()
    }


    suspend fun getTotalActiveNotification():Int=

        withContext(Dispatchers.IO)
        {
            try {

                val prayerNotificationData = prayerNotificationDao?.select_all_activate_notification()

               prayerNotificationData?.size?:0

            } catch (e: Exception) {
                return@withContext 0
            }
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

            val getStatus = getNotificationData(
                date = "",
                prayer_tag = "Notification",
                finalState = 0
            )

            if(getStatus?.state == 1)
            prayerNotificationDao?.select(date)
            else
                arrayListOf()
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


    suspend fun clearPrayerNotification():Int =
        withContext(Dispatchers.IO)
        {

            return@withContext prayerNotificationDao?.deleteAllNotification()?:0

            /*try {

                val prayerNotificationData = prayerNotificationDao?.select_all_activate_notification()

                if(prayerNotificationData?.isNotEmpty() == true)
                    //return@withContext prayerNotificationDao?.clearAllNotification(1)?:0
                    return@withContext prayerNotificationDao?.deleteAllNotification()?:0

                else
                    1

                }
            catch (e:Exception)
            {
                0
            }*/
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

    suspend fun refill_prayer_notification_for_alarm_service(prayerDate: String) {

            val getPrayerTime =  getPrayerTimeSDK("Dhaka",
                DeenSDKCore.GetDeenLanguage(), prayerDate)

            var prayerTimesResponse: PrayerTimesResponse? = null

            when (getPrayerTime) {
                is ApiResource.Failure -> {
                    Log.e("PRAYER_ALARAM_SERVICE","failed")
                }

                is ApiResource.Success -> {
                    prayerTimesResponse = getPrayerTime.value

                    Log.e("reCheckNotification1",Gson().toJson(prayerTimesResponse))

                    updatePrayerNotification(
                        prayerDate,
                        "pt1",
                        3,
                        "",
                        prayerTimesResponse
                    )


                    updatePrayerNotification(
                        prayerDate,
                        "pt3",
                        3,
                        "",
                        prayerTimesResponse
                    )

                    updatePrayerNotification(
                        prayerDate,
                        "pt4",
                        3,
                        "",
                        prayerTimesResponse
                    )

                    updatePrayerNotification(
                        prayerDate,
                        "pt5",
                        3,
                        "",
                        prayerTimesResponse
                    )

                    updatePrayerNotification(
                        prayerDate,
                        "pt6",
                        3,
                        "",
                        prayerTimesResponse
                    )
                }
            }

        }

    suspend fun setPrayerTimeTrackDateWise(
        language: String,
        prayer_tag: String,
        isPrayed: Boolean,
        prayerdate: String
    ) = makeApicall {
        val body = JSONObject()
        body.put("language", language)
        body.put(prayer_tag.lowercase(), isPrayed)
        body.put("Date", prayerdate)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        Log.e("setPrayerTimeTrack",body.toString())
        deenService?.setPrayerTimeTrackDateWise(parm = requestBody)
    }

    suspend fun getPrayerTracking(localtion:String,language:String,requiredDate:String)= makeApicall {
        val body = JSONObject()
        body.put("location", localtion)
        body.put("language", language)
        body.put("requiredDate", requiredDate)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getPrayerTracker(parm = requestBody)
    }
}