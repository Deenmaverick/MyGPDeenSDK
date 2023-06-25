package com.deenislam.sdk.service.libs.notification

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaPlayer

object AzanPlayer {

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
        }
        mMediaPlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        mMediaPlayer?.prepare()
        mMediaPlayer?.setOnPreparedListener {
            it?.start()
        }
    }

    fun releaseMediaPlayer() {
        mMediaPlayer?.stop()
        mMediaPlayer?.release()
        mMediaPlayer = null
    }

}