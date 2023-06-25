package com.deenislam.sdk.service.libs.notification

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.deenislam.sdk.R
import com.deenislam.sdk.service.database.dao.PrayerNotificationDao
import com.deenislam.sdk.service.database.entity.PrayerNotification
import com.deenislam.sdk.utils.sendNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Notificationservice:Service() {

    lateinit var prayerNotificationDao: PrayerNotificationDao
    private var mMediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        AzanPlayer.releaseMediaPlayer()

        val prayer_notification_id = intent?.extras?.getInt("pid",0)?:0

        if(prayer_notification_id>0)
            CoroutineScope(Dispatchers.IO).launch {
                processNotification(prayer_notification_id, this@Notificationservice)

            }

        val isNotificationDismiss = intent?.extras?.getString("dismiss").toString()

        if(isNotificationDismiss == "ok")
            AzanPlayer.releaseMediaPlayer()


        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
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


            }

        }
    }


    private suspend fun get_prayer_notification_data(pid:Int): PrayerNotification? =

        withContext(Dispatchers.IO)
        {
            if (this@Notificationservice::prayerNotificationDao.isInitialized) {

                val prayerNotificationData = prayerNotificationDao.select(pid)

                if (prayerNotificationData.isNotEmpty())
                    prayerNotificationData[0]
                else
                    null

            } else
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