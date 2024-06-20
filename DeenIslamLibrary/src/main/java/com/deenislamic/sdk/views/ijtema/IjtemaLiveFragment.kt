package com.deenislamic.sdk.views.ijtema

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.media3.ExoVideoManager
import com.deenislamic.sdk.service.libs.media3.VideoPlayerCallback
import com.deenislamic.sdk.service.models.PodcastResource
import com.deenislamic.sdk.service.network.response.common.CommonCardData
import com.deenislamic.sdk.service.repository.PodcastRepository
import com.deenislamic.sdk.service.repository.YoutubeVideoRepository
import com.deenislamic.sdk.service.repository.quran.learning.QuranLearningRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.get9DigitRandom
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.viewmodels.PodcastViewModel
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.launch


internal class IjtemaLiveFragment : BaseRegularFragment(), VideoPlayerCallback {

    private lateinit var playerView: PlayerView
    private lateinit var exoVideoManager: ExoVideoManager
    private val navArgs:IjtemaLiveFragmentArgs by navArgs()

    // player control

    private lateinit var vPlayerControlAction2: AppCompatImageView
    private lateinit var vPlayerControlBtnBack: AppCompatImageView
    private lateinit var vPlayerControlAction1: AppCompatImageView
    private lateinit var actionbar: ConstraintLayout
    private lateinit var viewmodel: PodcastViewModel
    //private var wakeLock: PowerManager.WakeLock? = null

    override fun OnCreate() {
        super.OnCreate()

        // init viewmodel
        val youtubeVideoRepository = YoutubeVideoRepository(
            youtubeService = NetworkProvider().getInstance().provideYoutubeService())
        val podcastRepository = PodcastRepository(deenService = NetworkProvider().getInstance().provideDeenService())

        val quranLearningRepository = QuranLearningRepository(
            quranShikkhaService = NetworkProvider().getInstance().provideQuranShikkhaService(),
            deenService = NetworkProvider().getInstance().provideDeenService(),
            dashboardService = NetworkProvider().getInstance().provideDashboardService()
        )

        viewmodel = PodcastViewModel(
            youtubeVideoRepository = youtubeVideoRepository,
            podcastRepository = podcastRepository,
            quranLearningRepository = quranLearningRepository
        )

        setupBackPressCallback(this,true)

        CallBackProvider.setFragment(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_ijtema_live,container,false)

        //init view

        vPlayerControlAction1 = mainview.findViewById(R.id.vPlayerControlAction1)
        vPlayerControlAction2 = mainview.findViewById(R.id.vPlayerControlAction2)
        playerView = mainview.findViewById(R.id.playerView)
        vPlayerControlBtnBack = mainview.findViewById(R.id.vPlayerControlBtnBack)
        actionbar = mainview.findViewById(R.id.actionbar)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = navArgs.title,
            isDarkActionBar = true,
            backEnable = true,
            view = mainview
        )

        exoVideoManager = ExoVideoManager(
            context = requireContext(),
            isLiveVideo = true,
            mainview = mainview
        )

        exoVideoManager.setupActionbar(isBackBtn = false, title = navArgs.title)
        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            setTrackingID(get9DigitRandom())
            userTrackViewModel.trackUser(
                language = getLanguage(),
                msisdn = DeenSDKCore.GetDeenMsisdn(),
                pagename = "live_ijtema",
                trackingID = getTrackingID()
            )
        }

       loadpage()

    }


    private fun loadpage()
    {
        vPlayerControlAction1.hide()

        vPlayerControlBtnBack.setOnClickListener {
            onBackPress()
        }

        vPlayerControlAction2.setOnClickListener {
            exoVideoManager.toggleFullScreen(requireActivity())
        }


        initObserver()
        loadLivePodcast()
    }

    private fun loadLivePodcast()
    {
        lifecycleScope.launch {
            viewmodel.getYoutubeVideoDetails(navArgs.videoid)
        }
    }

    private fun acquireWakeLock() {

       /* //This code holds the CPU
        val gfgPowerDraw = context?.getSystemService(POWER_SERVICE) as PowerManager?
        Log.e("gfgPowerDraw",gfgPowerDraw.toString())
        wakeLock = gfgPowerDraw?.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK ,
            "DeenSDK::AchieveWakeLock"
        )
        wakeLock?.acquire() // 20 minutes*/

        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }


    private fun releaseWakeLock() {
        /*wakeLock?.release()
        wakeLock = null*/
        Log.e("releaseWakeLock","OK")
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    }


    private fun initObserver()
    {
        viewmodel.podcastLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is PodcastResource.YoutubeVideoFetchFailed -> requireContext().toast("Failed to play live video")

                is PodcastResource.FetchLiveVideoUrl ->
                {
                    exoVideoManager.playLiveVideoFromUrl(url = it.VideoUrl, isAutoPlay = true)


                }
            }
        }
    }



    override fun onBackPress() {

        if(exoVideoManager.isVideoPlayerFullScreen()) {
            exoVideoManager.toggleFullScreen(requireActivity())
        }
        else {

            if(isVisible) {
                lifecycleScope.launch {
                    userTrackViewModel.trackUser(
                        language = getLanguage(),
                        msisdn = DeenSDKCore.GetDeenMsisdn(),
                        pagename = "live_ijtema",
                        trackingID = getTrackingID()
                    )
                }
            }
            super.onBackPress()
        }
    }


    override fun videoPlayerToggleFullScreen(isFullScreen: Boolean) {
        if(isFullScreen)
            actionbar.hide()
        else
            actionbar.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        exoVideoManager.onDestory()
    }


    override fun onPause() {
        super.onPause()
        exoVideoManager.pauseVideo()
        releaseWakeLock()
    }


    override fun onResume() {
        super.onResume()
        exoVideoManager.playPauseVideo()
    }

    override fun videoPlayerReady(isPlaying: Boolean, mediaData: CommonCardData?) {
        if(isPlaying)
            acquireWakeLock()
        /*else
            releaseWakeLock()*/
    }

    override fun pauseVideo(position: Int)
    {
        releaseWakeLock()
    }
}