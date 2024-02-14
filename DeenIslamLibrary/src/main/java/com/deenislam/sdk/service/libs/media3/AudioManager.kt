package com.deenislam.sdk.service.libs.media3;

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import com.deenislam.sdk.service.callback.AudioManagerBasicCallback
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.tryCatch

internal class AudioManager {

    private var mediaPlayer: MediaPlayer?= null
    private var apAdapterCallback:APAdapterCallback ? = null
    private var onlineAudio:Boolean = false
    private var audioManagerBasicCallback = CallBackProvider.get<AudioManagerBasicCallback>()
    private var audioUrl = ""
    companion object {
        var instance: AudioManager? = null
    }

    fun getInstance(): AudioManager {
        if (instance == null) {
            Log.e("AudioManager","getInstance")
            instance = AudioManager()
        }

        if(CallBackProvider.get<AudioManagerBasicCallback>()!=null)
            instance?.audioManagerBasicCallback = CallBackProvider.get<AudioManagerBasicCallback>()

        return instance as AudioManager
    }

    fun setCustomCallback(callback: AudioManagerBasicCallback)
    {
        instance?.audioManagerBasicCallback = callback
    }

    fun setupAdapterResponseCallback(apAdapterCallback: APAdapterCallback)
    {
        instance?.apAdapterCallback = apAdapterCallback
    }

    fun getMediaPlayer() = instance?.mediaPlayer

    fun getAudioUrl() = audioUrl

    fun playAudioFromUrl(url:String, position:Int = -1,isCallback:Boolean = true)
    {
        Log.e("playAudioFromUrl",url+" "+position)
        try {

            audioUrl = url

            releasePlayer(isCallback = isCallback)

            if(instance?.mediaPlayer == null)
                instance?.mediaPlayer = MediaPlayer()

            instance?.mediaPlayer?.apply {

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




            // instance?.mediaPlayer?.prepareAsync()

            instance?.mediaPlayer?.setOnPreparedListener {
                Log.e("MymediaPlayer","setOnPreparedListener")
                if(instance?.mediaPlayer?.isPlaying==false) {
                    if (position >= 0 && isCallback)
                        instance?.apAdapterCallback?.isPlaying(
                            position,
                            instance?.mediaPlayer?.duration?.toLong(),
                            0
                        )

                    if(isCallback)
                        audioManagerBasicCallback?.isMedia3Playing()

                    instance?.mediaPlayer?.start()

                }

            }

            instance?.mediaPlayer?.setOnErrorListener {mp, what, extra ->
                Log.e("mpsetOnErrorListener","called")
                //audioManagerBasicCallback?.isMedia3Pause()
                //instance?.apAdapterCallback?.isPause(position)
                when (what) {
                    MediaPlayer.MEDIA_ERROR_UNKNOWN,
                    MediaPlayer.MEDIA_ERROR_SERVER_DIED,
                    MediaPlayer.MEDIA_ERROR_IO,
                    MediaPlayer.MEDIA_ERROR_MALFORMED,
                    MediaPlayer.MEDIA_ERROR_UNSUPPORTED,
                    MediaPlayer.MEDIA_ERROR_TIMED_OUT-> {
                        audioManagerBasicCallback?.isMedia3Stop()
                    }

                }
                true
            }


            instance?.mediaPlayer?.setOnCompletionListener {
                if(isCallback)
                    audioManagerBasicCallback?.isMedia3PlayComplete()
                completePlaying(position)
            }


        }
        catch (e:Exception)
        {
            audioManagerBasicCallback?.isMedia3Stop()
            releasePlayer(position)
        }

    }

    fun playRawAudioFile(context: Context, file: Int,isCallback: Boolean = true)
    {
        try {

            releasePlayer(isCallback = isCallback)
            instance?.mediaPlayer = MediaPlayer.create(context, file).apply {
                start()
            }

        }
        catch (e:Exception)
        {
            releasePlayer()
        }
    }

    fun releasePlayer(position:Int=-1,crash:Boolean=false,isCallback: Boolean = true)
    {
        instance?.mediaPlayer?.reset()
        //instance?.mediaPlayer?.release()
        //instance?.mediaPlayer = null
        if(position>=0) {
            if(!crash && isCallback)
                instance?.apAdapterCallback?.isPause(position)
            else if(isCallback)
                instance?.apAdapterCallback?.isStop(position)
        }

        //Log.e("MymediaPlayer","release")
    }


    fun completePlaying(position:Int=-1)
    {
        instance?.mediaPlayer?.release()
        instance?.mediaPlayer = null
        if(position>=0)
            instance?.apAdapterCallback?.isComplete(position, 0)
    }

    fun pauseMediaPlayer(position:Int = -1)
    {
        instance?.mediaPlayer?.pause()
        audioManagerBasicCallback?.isMedia3Pause()
        if(position>=0)
            instance?.apAdapterCallback?.isPause(position)

    }

    fun resumeMediaPlayer()
    {
        instance?.mediaPlayer?.start()
        audioManagerBasicCallback?.isMedia3Playing()

    }



    fun stopMediaPlayer(position:Int = -1)
    {
        instance?.mediaPlayer?.stop()

        if(position>=0)
            instance?.apAdapterCallback?.isStop(position)

        audioManagerBasicCallback?.isMedia3Stop()
    }

    fun startMediaPlayer(position:Int = -1)
    {
        instance?.mediaPlayer?.start()

        if(position>=0)
            instance?.apAdapterCallback?.isPlaying(
                duration = instance?.mediaPlayer?.duration?.toLong(),
                surahID = 0
            )
    }

}
