package com.deenislam.sdk.service.libs.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
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

class AlarmReceiver : BroadcastReceiver() {

    private var mMediaPlayer: MediaPlayer? = null

    override fun onReceive(context: Context, intent: Intent) {

        if(DeenSDKCore.appContext == null)
        DeenSDKCore.appContext = context.applicationContext
        if(DeenSDKCore.baseContext == null)
        DeenSDKCore.baseContext = context
        if(DeenSDKCore.token.isEmpty())
            DeenSDKCore.token = "MyBLSDK"

        AzanPlayer.releaseMediaPlayer()

        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {

            }
            else ->
            {

               /* val service = Intent(context, AlarmReceiverService::class.java)
                service.putExtra("pid",intent.extras?.getInt("pid",0)?:0 )
                service.putExtra("dismiss",intent.extras?.getString("dismiss").toString() )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(service)
                } else {
                    context.startService(service)
                }*/
             /*   val service = Intent(context, Notificationservice::class.java)
                service.putExtra("pid",intent.extras?.getInt("pid",0)?:0 )
                service.putExtra("dismiss",intent.extras?.getString("dismiss").toString() )
                context.startService(service)
*/
                val prayer_notification_id = intent.extras?.getInt("pid",0)?:0

                Log.e("AlarmReceiver_Prayer", prayer_notification_id.toString())

                if(prayer_notification_id>0) {
                    CoroutineScope(Dispatchers.IO).launch {
                        processNotification(prayer_notification_id, context)

                    }
                }

                val isNotificationDismiss = intent.extras?.getString("dismiss").toString()

                if(isNotificationDismiss == "ok")
                    AzanPlayer.releaseMediaPlayer()

            }
        }

    }


    private suspend fun processNotification(prayer_notification_id: Int, context: Context)
    {
            get_prayer_notification_data(prayer_notification_id)?.let {

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


                    clear_prayer_notification_by_id(it.id)

                    val inputData = Data.Builder()
                        .putString("prayerDate", it.date.StringTimeToMillisecond("dd/MM/yyyy").MilliSecondToStringTime("dd/MM/yyyy",1))
                        .build()

                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()

                    val workRequest = OneTimeWorkRequestBuilder<AlarmReceiverWork>()
                        .setConstraints(constraints)
                        .setInputData(inputData)
                        .build()

                    WorkManager.getInstance(context).enqueue(workRequest)
                    //reCheckNotification(it.date.StringTimeToMillisecond("dd/MM/yyyy").MilliSecondToStringTime("dd/MM/yyyy",1))

                }

            }
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

    private suspend fun reCheckNotification(prayerDate: String)
    {

    }

    private suspend fun clear_prayer_notification_by_id(pid:Int) =

        withContext(Dispatchers.IO)
        {
            val prayerNotificationDao = DatabaseProvider().getInstance().providePrayerNotificationDao()

            prayerNotificationDao?.clearNotificationByID(pid)

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

            "opt1"-> "Tahajjud"

            "opt2"-> "Suhoor"

            "opt3"-> "Ishraq"

            "opt4"-> "Iftaar"

            else -> null

        }

    // Media Player For playing azan
    fun playAdanFromRawFolder(context: Context, id: Int) {

        //mMediaPlayer = MediaPlayer.create(context, id)
        val assetFileDescriptor: AssetFileDescriptor = context.resources.openRawResourceFd(id)
        mMediaPlayer = MediaPlayer().apply {
            setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.length
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaPlayer?.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
        }
        else
            mMediaPlayer?.setAudioStreamType(AudioManager.STREAM_ALARM)


        mMediaPlayer?.prepare()
        mMediaPlayer?.setOnPreparedListener {
            it?.start()
        }
        mMediaPlayer?.setOnCompletionListener {
            if (!it.isPlaying()) {
                it.release();
            }
            else {
                it.stop();
                it.release();
            }
        }

        Log.e("ALARM_INTENT","Azan called")

    }

    fun releaseMediaPlayer() {
        mMediaPlayer?.stop()
        mMediaPlayer?.release()
        mMediaPlayer = null
    }

}