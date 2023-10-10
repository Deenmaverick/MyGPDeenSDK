package com.deenislam.sdk.service.libs.notification

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import com.deenislam.sdk.service.libs.media3.AudioManager
import com.deenislam.sdk.utils.tryCatch

internal object AzanPlayer {

    private var mMediaPlayer: MediaPlayer? = null

    fun playAdanFromRawFolder(context: Context, id: Int) {
        releaseMediaPlayer()
        //mMediaPlayer = MediaPlayer.create(context, id)
        val assetFileDescriptor: AssetFileDescriptor = context.resources.openRawResourceFd(id)
        mMediaPlayer = MediaPlayer().apply {
            setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.length
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            } else {

                setAudioStreamType(android.media.AudioManager.STREAM_ALARM)  // Deprecated method, but necessary for older devices
            }
            prepareAsync()
            start()
        }

        mMediaPlayer?.setOnPreparedListener {
            it.start()
        }


    }

    fun playAdanFromUrl(url:String)
    {
        try {

            releaseMediaPlayer()

            if(mMediaPlayer == null)
                mMediaPlayer = MediaPlayer()

            mMediaPlayer?.apply {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val attributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                    setAudioAttributes(attributes)
                } else {
                    setAudioStreamType(android.media.AudioManager.STREAM_MUSIC)  // Deprecated method, but necessary for older devices
                }


                setDataSource(url)
                prepareAsync()
            }



            mMediaPlayer?.setOnPreparedListener {
                it?.start()
            }

        }
        catch (e:Exception)
        {
            Log.e("AzanPlayer",e.toString())
        }

    }


    fun releaseMediaPlayer() {
        tryCatch {
            //mMediaPlayer?.stop()
            mMediaPlayer?.reset()
            //mMediaPlayer?.release()
            //mMediaPlayer = null
        }
    }

}