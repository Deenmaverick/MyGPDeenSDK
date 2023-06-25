package com.deenislam.sdk.service.libs.media3;

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import com.deenislam.sdk.R

class AudioManager {

    private var mediaPlayer: MediaPlayer?= null
    private var apAdapterCallback:APAdapterCallback ? = null

    companion object {
        var instance: AudioManager? = null
    }

    fun getInstance(): AudioManager {
        if (instance == null)
            instance = AudioManager()

        return instance as AudioManager
    }

    fun setupAdapterResponseCallback(apAdapterCallback: APAdapterCallback)
    {
        Log.e("setupAdapterResponseCallback","ok")
        instance?.apAdapterCallback = apAdapterCallback
    }

    fun playAudioFromUrl(url:String,position:Int = -1)
    {
        try {

            releasePlayer()

                instance?.mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(url)
                    prepare()
                }
        }
        catch (e:Exception)
        {
            Log.e("playAudioFromUrl",url)
            releasePlayer()
        }

        instance?.mediaPlayer?.setOnPreparedListener {
            instance?.mediaPlayer?.start()
            if(position>=0)
                apAdapterCallback?.isPlaying(position)
        }

    }

    fun playRawAudioFile(context: Context, file: Int)
    {
        try {

            releasePlayer()
            instance?.mediaPlayer = MediaPlayer.create(context, file).apply {
                start()
            }

        }
        catch (e:Exception)
        {
            releasePlayer()
        }
    }

     fun releasePlayer(position:Int=-1)
    {
        instance?.mediaPlayer?.release()
        instance?.mediaPlayer = null
        if(position>=0)
            apAdapterCallback?.isStop(position)
    }

    fun pauseMediaPlayer(position:Int = -1)
    {
        instance?.mediaPlayer?.pause()

        if(position>=0)
            apAdapterCallback?.isPause(position)
    }

    fun startMediaPlayer(position:Int = -1)
    {
        instance?.mediaPlayer?.start()

        if(position>=0)
            apAdapterCallback?.isPlaying()
    }

}
