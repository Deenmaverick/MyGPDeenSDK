package com.deenislam.sdk.service.libs.media3;

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.common.CommonCardData
import com.deenislam.sdk.service.network.response.common.toMediaItems
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.bottomControlHideWithAnim
import com.deenislam.sdk.utils.bottomControlShowWithAnim
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.enterFullScreen
import com.deenislam.sdk.utils.exitFullScreen
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.topControlHideWithAnim
import com.deenislam.sdk.utils.topControlShowWithAnim
import com.deenislam.sdk.utils.visible
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.TimeBar
import com.google.android.exoplayer2.upstream.DefaultDataSource


internal class ExoVideoManager(
    context: Context,
    private val isLiveVideo: Boolean,
    mainview: View
) {

    private var exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    private val callback = CallBackProvider.get<VideoPlayerCallback>()
    private var isVideoListAutoPlay = false
    private var initialPlayerStart = false
    private val btnNext: AppCompatImageView? by lazy { mainview.findViewById(R.id.exo_next) }
    private val btnPrev: AppCompatImageView? by lazy {  mainview.findViewById(R.id.exo_prev) }

    // video player
    private var isFullScreen = false
    private var isBackBtn = false
    private var isPauseClicked = false

    private var storedVideoList:ArrayList<CommonCardData>? = null

    private var isControllerShowing = false


    val hideControlsRunnable = Runnable {
        isControllerShowing = false
        videoPlayerControlHide()
        //playerView.hideController()
    }

    private val updateExoProgressRunnable = object : Runnable {
        override fun run() {
            exo_progress.setPosition(exoPlayer.currentPosition)
            exo_progress.setBufferedPosition(exoPlayer.bufferedPosition)
            handler.postDelayed(this, 1000) // Update every second
        }
    }
    val handler = Handler(Looper.getMainLooper())
    private var isUserInteractingWithTimeBar = false


    // player control
    private val playerView: PlayerView = mainview.findViewById(R.id.playerView)
    private val vPlayerControlBtnBack: AppCompatImageView = mainview.findViewById(R.id.vPlayerControlBtnBack)
    private val vPlayerControlTitle: AppCompatTextView = mainview.findViewById(R.id.vPlayerControlTitle)
    private val vPlayerControlAction2: AppCompatImageView = mainview.findViewById(R.id.vPlayerControlAction2)

    private val vPlayerControlBtnPlay: AppCompatImageView = mainview.findViewById(R.id.vPlayerControlBtnPlay)
    private val vPlayerControlTxtLive: AppCompatTextView? = mainview.findViewById(R.id.vPlayerControlTxtLive)
    private val exo_position: AppCompatTextView = mainview.findViewById(R.id.exo_position)
    private val exo_progress: CustomTimeBar = mainview.findViewById(R.id.custom_exo_progress)

    private val vPlayerTopLayer: ConstraintLayout = mainview.findViewById(R.id.vPlayerTopLayer)
    private val vPlayerBottomLayer: ConstraintLayout = mainview.findViewById(R.id.vPlayerBottomLayer)
    private val vPlayerCenterLayer: ConstraintLayout? = mainview.findViewById(R.id.vPlayerCenterLayer)

    init {

        setupVideoType(isLiveVideo)
        setupCustomControlBehavior()
        vPlayerControlBtnPlay.setOnClickListener {
            playPauseVideo()
        }
        playerView.controllerAutoShow = false

    }


    fun playRegularVideoFromUrl(
        videoList: ArrayList<CommonCardData>? = null,
        singleVideoUrl: String? = null,
        initialPlayerStart: Boolean = false,
        isVideoAutoPlay: Boolean = false,
        startFromDuration:Int = 0,
        mediaID:String = ""
    ) {

        this.initialPlayerStart = initialPlayerStart
        this.isVideoListAutoPlay  = isVideoAutoPlay
        exoPlayer.stop()

        storedVideoList = videoList

        if (playerView.player == null)
            playerView.player = exoPlayer

        if (singleVideoUrl != null) {
            val mediaItem = MediaItem.Builder()
                .setUri(singleVideoUrl)
            if(mediaID.isNotEmpty())
                mediaItem.setMediaId(mediaID)

            exoPlayer.setMediaItem(mediaItem.build())
        } else if (storedVideoList != null) {
            exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
            playerView.player?.setMediaItems(storedVideoList!!.toMediaItems(), /* resetPosition= */ false)
        } else
            return


        // prev / next btn click

        btnPrev?.setOnClickListener {
            if (exoPlayer.hasPreviousMediaItem()) {
                exoPlayer.seekToPreviousMediaItem()
            }
            Log.e("ExoPlayer", "HELLO")
        }

        btnNext?.setOnClickListener {
            if (exoPlayer.hasNextMediaItem()) {
                exoPlayer.seekToNextMediaItem()
            }
            Log.e("ExoPlayer", "HELLO")
        }

        exoPlayer.prepare()
        if(startFromDuration>0)
        exoPlayer.seekTo((startFromDuration * 1000).toLong())
        //exoPlayer.pauseAtEndOfMediaItems = true
        var isInitialPlay = initialPlayerStart
        exoPlayer.addListener(object : Player.Listener {

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    when (reason) {
                        Player.MEDIA_ITEM_TRANSITION_REASON_AUTO -> {
                            // Happened automatically, for instance, previous item finished.
                            Log.e("ExoPlayer", "Transitioned to mediaItem automatically: ${mediaItem?.mediaId} ${isVideoListAutoPlay}")
                            if(!isVideoListAutoPlay) {
                                exoPlayer.pause()
                                exoPlayer.seekToPrevious()
                                exoPlayer.playWhenReady = false
                            }
                            else
                                onIsPlayingChanged(true)

                        }
                        Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED -> {
                            // Playlist was changed.
                            Log.e("ExoPlayer", "Transitioned to mediaItem due to playlist change: ${mediaItem?.mediaId}")
                        }
                        Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT -> {
                            // Repeating the current item.
                            Log.e("ExoPlayer", "Repeating mediaItem: ${mediaItem?.mediaId}")
                        }
                        Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> {
                            // Happened due to a seek.
                            Log.e("ExoPlayer", "Transitioned to mediaItem due to seek: ${mediaItem?.mediaId}")
                        }
                    }
                }


            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                Log.e("onIsPlayingChanged", isPlaying.toString())

                callback?.videoPlayerReady(
                    isPlaying,
                    storedVideoList?.find { it.Id.toString() == exoPlayer.currentMediaItem?.mediaId }
                )
                playPauseIconSync(isPlaying)
                exo_progress.setPosition(exoPlayer.currentPosition)
                exo_progress.setBufferedPosition(exoPlayer.bufferedPosition)
                if (isPlaying) {
                    handler.post(updateExoProgressRunnable)  // Start the periodic updates when playing
                } else {
                    handler.removeCallbacks(updateExoProgressRunnable)  // Stop the updates when paused
                }
            }


            override fun onPlaybackStateChanged(playbackState: Int) {

                val position = exoPlayer.currentPosition
                val duration = exoPlayer.duration
                exo_progress.setPosition(position)
                exo_progress.setDuration(duration)

                if (playbackState == ExoPlayer.STATE_READY) {
                    if (this@ExoVideoManager.initialPlayerStart) {
                        exoPlayer.play() // Start playback when ready
                        isInitialPlay = false
                    }

                }

                if (playbackState == ExoPlayer.STATE_ENDED) {
                    exoPlayer.pause()
                    exoPlayer.seekTo(0)
                    exoPlayer.playWhenReady = false
                    vPlayerControlBtnPlay.setImageResource(R.drawable.ic_play_fill)
                    callback?.videoPlayerEnded()
                }

            }

        })
    }

    fun setAutoPlayVideoList(bol:Boolean)
    {
        isVideoListAutoPlay = bol
    }

    fun setInitialPlayerStart(bol:Boolean)
    {
        this.initialPlayerStart = bol
    }

    fun playVideoFromList(getData: CommonCardData)
    {
        if(exoPlayer.currentMediaItem?.mediaId == getData.Id.toString())
            playPauseVideo()
        else {
            val matchingVideoDataIndex = storedVideoList?.indexOfFirst { it.Id == getData.Id }

            if (matchingVideoDataIndex != null) {
                playerView.player?.seekTo(matchingVideoDataIndex, 0L)
            }
        }
    }


    fun playLiveVideoFromUrl(url: String, isAutoPlay: Boolean = false) {
        exoPlayer.stop()

        // Initialize ExoPlayer

        /*  val loadControl = DefaultLoadControl.Builder()
              .setTargetBufferBytes(DefaultLoadControl.DEFAULT_MAX_BUFFER_MS*2) // or another value
              .setPrioritizeTimeOverSizeThresholds(DefaultLoadControl.DEFAULT_PRIORITIZE_TIME_OVER_SIZE_THRESHOLDS)
              .setBackBuffer(DefaultLoadControl.DEFAULT_BACK_BUFFER_DURATION_MS, DefaultLoadControl.DEFAULT_RETAIN_BACK_BUFFER_FROM_KEYFRAME)
              .setBufferDurationsMs(
                  DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                  DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                  DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                  DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
              )
              .build()*/


        if (playerView.player == null)
            playerView.player = exoPlayer


        // Prepare the player with a source.
        /*val dataSourceFactory = DefaultDataSource.Factory(playerView.context)

        val dashChunkSourceFactory = DefaultDashChunkSource.Factory(dataSourceFactory)
        val mediaSourceFactory = HlsMediaSource.Factory(dashChunkSourceFactory, dataSourceFactory)

        val dashUri = Uri.parse(url)
        val mediaItem = MediaItem.fromUri(dashUri)
        val mediaSource = mediaSourceFactory.createMediaSource(mediaItem)
        exoPlayer?.setMediaSource(mediaSource)
        exoPlayer?.prepare()*/

        val dataSourceFactory = DefaultDataSource.Factory(playerView.context)

        val hlsMediaSourceFactory = HlsMediaSource.Factory(dataSourceFactory)

        val hlsUri = Uri.parse(url) // Assuming your URL is an m3u8 URL
        val mediaItem = MediaItem.fromUri(hlsUri)
        val mediaSource = hlsMediaSourceFactory.createMediaSource(mediaItem)

        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()


            // Listen for when the media is ready to be played
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == ExoPlayer.STATE_READY) {
                        if (isAutoPlay)
                        exoPlayer.play() // Start playback when ready
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    playPauseIconSync(isPlaying)
                }
            })

    }

    fun getExoPlayer() = exoPlayer

    private fun pauseVideoPlayer() {
        exoPlayer.pause()
    }

    private fun playVideoPlayer() {
        exoPlayer.play()
    }

    fun playPauseVideo() {
        if (exoPlayer.isPlaying) {
            isPauseClicked = true
            pauseVideoPlayer()
        }
        else
            playVideoPlayer()
    }


    fun releasePlayer() {
        exoPlayer.release()
    }

    fun onDestory()
    {
        releasePlayer()
        handler.removeCallbacks(updateExoProgressRunnable)
        handler.removeCallbacks(hideControlsRunnable)
    }


    // Video Player

    fun setupVideoType(isLiveVideo: Boolean)
    {
        if(isLiveVideo)
        {
            vPlayerControlTxtLive?.show()
            exo_progress.hide()
            exo_position.hide()
        }
        else
        {
            vPlayerControlTxtLive?.hide()
            exo_progress.show()
            exo_position.show()
        }

        if(!isFullScreen && !isBackBtn)
            vPlayerControlBtnBack.hide()
        else
            vPlayerControlBtnBack.show()
    }


    private fun hideAllControls() {
        videoPlayerControlHide()
        playerView.hideController()
    }



    private fun setupCustomControlBehavior() {


        playerView.setOnTouchListener { _, event ->
            val inTimeBarBounds = event.x >= exo_progress.left && event.x <= exo_progress.right &&
                    event.y >= exo_progress.top && event.y <= exo_progress.bottom

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (inTimeBarBounds) {
                        isUserInteractingWithTimeBar = true
                    }
                    handler.removeCallbacks(hideControlsRunnable)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (inTimeBarBounds) {
                        isUserInteractingWithTimeBar = true
                    }
                    handler.removeCallbacks(hideControlsRunnable)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isUserInteractingWithTimeBar) {
                        isUserInteractingWithTimeBar = false
                        // Restart the countdown to hide controls when user stops interacting with the time bar
                        handler.postDelayed(hideControlsRunnable, 5000) // 5 seconds
                    }
                }
            }
            false // return false to let the event propagate further
        }

        playerView.setOnClickListener {
            isControllerShowing = if (isControllerShowing) {
                hideAllControls()
                false
            } else {
                videoPlayerControlShow()
                playerView.showController()
                true
            }
            setupVideoType(isLiveVideo = isLiveVideo)
        }


          exo_progress.setScrubListener(object : TimeBar.OnScrubListener {
              override fun onScrubMove(timeBar: TimeBar, position: Long) {
                  // Seek the ExoPlayer to the scrubber's position
                  exoPlayer.seekTo(position)
              }

              override fun onScrubStart(timeBar: TimeBar, position: Long) {
                  // Optional: You can pause the ExoPlayer while scrubbing
                  exoPlayer.playWhenReady = false
              }

              override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                  // Resume playback after scrubbing (if it was playing before)
                  exoPlayer.playWhenReady = true
              }
          })
    }




    private fun videoPlayerControlHide()
    {
        topControlHideWithAnim(vPlayerTopLayer)

        vPlayerCenterLayer?.let {
            topControlHideWithAnim(it)
        }

        bottomControlHideWithAnim(vPlayerBottomLayer)
    }

    private fun videoPlayerControlShow()
    {
        topControlShowWithAnim(vPlayerTopLayer)

        vPlayerCenterLayer?.let {
            topControlShowWithAnim(it)
        }

        bottomControlShowWithAnim(vPlayerBottomLayer)
    }

    private fun controlsRunnable(isRemoved:Boolean = false)
    {
        if(isRemoved)
            handler.removeCallbacks(hideControlsRunnable)
        else
            handler.postDelayed(hideControlsRunnable, 5000)
    }

    fun toggleFullScreen(activity: Activity) {

        if (!isFullScreen) {

            // Go full-screen in landscape mode
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            val params = playerView.layoutParams as ConstraintLayout.LayoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            playerView.layoutParams = params
            activity.enterFullScreen(vPlayerTopLayer,vPlayerBottomLayer)
            vPlayerControlAction2.setImageResource(R.drawable.ic_fullscreen_exit)
            isFullScreen = true

            (vPlayerControlTitle.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 8.dp
        } else {

            // Exit full-screen and revert to the original orientation
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            val params = playerView.layoutParams as ConstraintLayout.LayoutParams
            params.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            params.height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT  // Adjust the height as needed
            playerView.layoutParams = params
            activity.exitFullScreen(vPlayerTopLayer,vPlayerBottomLayer)
            vPlayerControlAction2.setImageResource(R.drawable.ic_fullscreen)
            isFullScreen = false

            if(!isBackBtn)
                (vPlayerControlTitle.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 16.dp


        }

        setupVideoType(isLiveVideo)
        callback?.videoPlayerToggleFullScreen(isFullScreen)
    }

    fun setupActionbar(isBackBtn:Boolean=false,title:String)
    {
        this.isBackBtn = isBackBtn
        vPlayerControlBtnBack.visible(isBackBtn)
        vPlayerControlTitle.text = title

        if(!isBackBtn)
            (vPlayerControlTitle.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 16.dp
    }

    fun isVideoPlayerFullScreen() = isFullScreen


    private fun playPauseIconSync(isPlaying:Boolean)
    {
        when(isPlaying)
        {
            true ->
            {
                if (exoPlayer.isCurrentMediaItemLive && isPauseClicked) {
                    isPauseClicked = false
                    exoPlayer.seekToDefaultPosition()
                }
                vPlayerControlBtnPlay.setImageResource(R.drawable.ic_pause_fill)
                controlsRunnable()
            }
            false->
            {
                vPlayerControlBtnPlay.setImageResource(R.drawable.ic_play_fill)
                controlsRunnable(true)
            }
        }
    }

}