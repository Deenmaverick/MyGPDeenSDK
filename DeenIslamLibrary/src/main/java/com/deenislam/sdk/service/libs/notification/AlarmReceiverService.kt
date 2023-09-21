package com.deenislam.sdk.service.libs.notification

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.service.database.entity.PrayerNotification
import com.deenislam.sdk.service.di.DatabaseProvider
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.repository.PrayerTimesRepository
import com.deenislam.sdk.utils.MilliSecondToStringTime
import com.deenislam.sdk.utils.StringTimeToMillisecond
import com.deenislam.sdk.utils.sendNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class AlarmReceiverService: Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val serviceContext:Context = this
        if(DeenSDKCore.appContext == null)
            DeenSDKCore.appContext = this.applicationContext
        if(DeenSDKCore.baseContext == null)
            DeenSDKCore.baseContext = this
        if(DeenSDKCore.GetDeenToken().isEmpty())
            DeenSDKCore.SetDeenToken("MyBLSDK")

        AzanPlayer.releaseMediaPlayer()

        val prayer_notification_id = intent?.extras?.getInt("pid",0)?:0

        Log.e("AlarmReceiver_Prayer", prayer_notification_id.toString())

        if(prayer_notification_id>0) {
            CoroutineScope(Dispatchers.IO).launch {
                processNotification(prayer_notification_id, serviceContext)
            }
        }

        val isNotificationDismiss = intent?.extras?.getString("dismiss").toString()

        if(isNotificationDismiss == "ok")
            AzanPlayer.releaseMediaPlayer()


        return super.onStartCommand(intent, flags, startId)
    }


    private suspend fun processNotification(prayer_notification_id: Int, context: Context)
    {
        get_prayer_notification_data(prayer_notification_id)?.let {

            if(it.state!=3 && it.state!=2)
                return


            val prayerName = get_prayer_name_by_tag(it.prayer_tag)

            prayerName?.let {
                    name->
                // sendNotification
                val notificationManager = ContextCompat.getSystemService(
                    context,
                    NotificationManager::class.java
                ) as NotificationManager

                notificationManager.sendNotification(
                    applicationContext = context,
                    title = "Prayer Alert!",
                    messageBody = "it's time for $prayerName",
                    largeImg = null,
                    channelID = "Prayer Time",
                    notification_id = prayer_notification_id
                )

                if(it.state == 3) {

                    if (prayerName == "Fajr")
                        AzanPlayer.playAdanFromRawFolder(context, R.raw.azan_common_fajr)
                    else
                        AzanPlayer.playAdanFromRawFolder(context, R.raw.azan_common)

                }


                val pid = it.id
                val prayerDate = it.date.StringTimeToMillisecond("dd/MM/yyyy").MilliSecondToStringTime("dd/MM/yyyy",1)

                clear_prayer_notification_by_id(pid)

                if(prayerName == "Isha") {
                    val prayerTimesRepository = PrayerTimesRepository(
                        deenService = NetworkProvider().getInstance().provideDeenService(),
                        prayerNotificationDao = DatabaseProvider().getInstance()
                            .providePrayerNotificationDao(),
                        prayerTimesDao = null
                    )

                    prayerTimesRepository.refill_prayer_notification_for_alarm_service(prayerDate)
                }

            }

        }
    }

    private suspend fun clear_prayer_notification_by_id(pid:Int) =

        withContext(Dispatchers.IO)
        {
            val prayerNotificationDao = DatabaseProvider().getInstance().providePrayerNotificationDao()

            prayerNotificationDao?.clearNotificationByID(pid)

        }

    private suspend fun get_prayer_notification_data(pid:Int): PrayerNotification? =

        withContext(Dispatchers.IO)
        {

            val prayerNotificationDao = DatabaseProvider().getInstance().providePrayerNotificationDao()

            val prayerNotificationData = prayerNotificationDao?.select(pid)

            if (prayerNotificationData?.isNotEmpty() == true)
                prayerNotificationData[0]
            else
                null

        }


    private fun get_prayer_name_by_tag(tag:String): String? =
        when(tag)
        {
            "pt1"-> "Fajr"
            "pt2"-> "Sunrise"

            "pt3"-> "Dhuhr"

            "pt4"-> "Asr"

            "pt5"-> "Maghrib"

            "pt6"-> "Isha"

            "opt3"-> "Tahajjud"

            "opt1"-> "Suhoor"

            "opt4"-> "Ishraq"

            "opt2"-> "Iftaar"

            else -> null

        }

    override fun onDestroy() {
        super.onDestroy()
        AzanPlayer.releaseMediaPlayer()
    }
}