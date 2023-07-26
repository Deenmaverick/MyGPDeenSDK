package com.deenislam.sdk.service.libs.media3;

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

internal class AudioManager {

    private var mediaPlayer: MediaPlayer?= null
    private var apAdapterCallback:APAdapterCallback ? = null
    private var onlineAudio:Boolean = false

    companion object {
        var instance: AudioManager? = null
    }

    fun getInstance(): AudioManager {
        if (instance == null) {
            Log.e("AudioManager","getInstance")
            instance = AudioManager()
        }

        return instance as AudioManager
    }

    fun setupAdapterResponseCallback(apAdapterCallback: APAdapterCallback)
    {
        instance?.apAdapterCallback = apAdapterCallback
    }

    fun playAudioFromUrl(url:String, position:Int = -1)
    {
        Log.e("playAudioFromUrl",url+" "+position)
        try {
            releasePlayer()

            if(instance?.mediaPlayer == null)
                instance?.mediaPlayer = MediaPlayer()

            instance?.mediaPlayer?.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                prepareAsync()
            }


            // instance?.mediaPlayer?.prepareAsync()

            instance?.mediaPlayer?.setOnPreparedListener {
                Log.e("MymediaPlayer","setOnPreparedListener")
                if(instance?.mediaPlayer?.isPlaying==false) {
                    if (position >= 0)
                        instance?.apAdapterCallback?.isPlaying(position, instance?.mediaPlayer?.duration)
                    instance?.mediaPlayer?.start()

                }
            }

            instance?.mediaPlayer?.setOnCompletionListener {

                completePlaying(position)
            }


        }
        catch (e:Exception)
        {
            releasePlayer(position)
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

    fun releasePlayer(position:Int=-1,crash:Boolean=false)
    {
        instance?.mediaPlayer?.reset()
        //instance?.mediaPlayer?.release()
        //instance?.mediaPlayer = null
        if(position>=0) {
            if(!crash)
                instance?.apAdapterCallback?.isPause(position)
            else
                instance?.apAdapterCallback?.isStop(position)
        }

        //Log.e("MymediaPlayer","release")
    }


    fun completePlaying(position:Int=-1)
    {
        instance?.mediaPlayer?.release()
        instance?.mediaPlayer = null
        if(position>=0)
            instance?.apAdapterCallback?.isComplete(position)
    }

    fun pauseMediaPlayer(position:Int = -1)
    {
        instance?.mediaPlayer?.pause()

        if(position>=0)
            instance?.apAdapterCallback?.isPause(position)
    }

    fun stopMediaPlayer(position:Int = -1)
    {
        instance?.mediaPlayer?.stop()

        if(position>=0)
            instance?.apAdapterCallback?.isStop(position)
    }

    fun startMediaPlayer(position:Int = -1)
    {
        instance?.mediaPlayer?.start()

        if(position>=0)
            instance?.apAdapterCallback?.isPlaying(duration = instance?.mediaPlayer?.duration)
    }

}
