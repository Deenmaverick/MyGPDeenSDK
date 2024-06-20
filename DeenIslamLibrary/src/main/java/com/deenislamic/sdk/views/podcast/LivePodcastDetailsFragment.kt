package com.deenislamic.sdk.views.podcast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.PodcastCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.media3.ExoVideoManager
import com.deenislamic.sdk.service.libs.media3.VideoPlayerCallback
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.PodcastResource
import com.deenislamic.sdk.service.network.response.common.CommonCardData
import com.deenislamic.sdk.service.network.response.podcast.comment.Comment
import com.deenislamic.sdk.service.repository.PodcastRepository
import com.deenislamic.sdk.service.repository.YoutubeVideoRepository
import com.deenislamic.sdk.service.repository.quran.learning.QuranLearningRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.viewmodels.PodcastViewModel
import com.deenislamic.sdk.views.adapters.podcast.LivePodcastChatAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch


internal class LivePodcastDetailsFragment : BaseRegularFragment(), VideoPlayerCallback,
    PodcastCallback {

    private lateinit var chatList:RecyclerView
    private lateinit var playerView: PlayerView
    private lateinit var bottom_nav:CoordinatorLayout
    private lateinit var liveVideoProgress:View
    private lateinit var chatActionbar: ConstraintLayout
    private lateinit var chat_count:AppCompatTextView
    private lateinit var ask_input:TextInputEditText
    private lateinit var sendBtn:FloatingActionButton
    // player control
    private lateinit var vPlayerControlAction1: AppCompatImageView
    private lateinit var vPlayerControlAction2: AppCompatImageView
    private lateinit var vPlayerControlBtnBack:AppCompatImageView

    private lateinit var exoVideoManager: ExoVideoManager

    private lateinit var actionbar:ConstraintLayout
    private lateinit var livePodcastChatAdapter: LivePodcastChatAdapter

    private lateinit var viewmodel: PodcastViewModel
    private val navArgs:LivePodcastDetailsFragmentArgs by navArgs()

    override fun OnCreate() {
        super.OnCreate()

        CallBackProvider.setFragment(this)

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
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainview = localInflater.inflate(R.layout.fragment_live_podcast_details,container,false)

        //init view

        chatList = mainview.findViewById(R.id.chatList)
        playerView = mainview.findViewById(R.id.playerView)
        bottom_nav = mainview.findViewById(R.id.bottom_nav)
        chatActionbar = mainview.findViewById(R.id.chatActionbar)
        actionbar = mainview.findViewById(R.id.actionbar)
        vPlayerControlBtnBack = mainview.findViewById(R.id.vPlayerControlBtnBack)
        liveVideoProgress = mainview.findViewById(R.id.liveVideoProgress)
        vPlayerControlAction1 = mainview.findViewById(R.id.vPlayerControlAction1)
        vPlayerControlAction2 = mainview.findViewById(R.id.vPlayerControlAction2)
        chat_count = chatActionbar.findViewById(R.id.chat_count)
        ask_input = bottom_nav.findViewById(R.id.ask_input)
        sendBtn = bottom_nav.findViewById(R.id.sendBtn)
        exoVideoManager = ExoVideoManager(
            context = requireContext(),
            isLiveVideo = true,
            mainview = mainview
        )
        exoVideoManager.setupActionbar(isBackBtn = true, title = navArgs.title)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = navArgs.title,
            backEnable = true,
            view = mainview
        )

        setupCommonLayout(mainview)

        vPlayerControlAction1.hide()

        return mainview
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackPressCallback(this)

      /*  if (!isDetached) {
            view.postDelayed({
                loadPage()
            }, 300)
        }
        else*/
            loadPage()


    }


    private fun loadPage(){

        sendBtn.setOnClickListener {

            if(ask_input.text.toString().length<5){
                context?.toast("Comment required minimum 5 character")
                return@setOnClickListener
            }
            sendBtn.isClickable = false
            lifecycleScope.launch {
                viewmodel.addComment(navArgs.pid,ask_input.text.toString())
            }
        }

        chatList.apply {
            livePodcastChatAdapter = LivePodcastChatAdapter()
            adapter = livePodcastChatAdapter
            isNestedScrollingEnabled = false
        }

        vPlayerControlAction2.setOnClickListener {
            exoVideoManager.toggleFullScreen(requireActivity())
        }

        vPlayerControlBtnBack.setOnClickListener {
            onBackPress()
        }

        initObserver()
        loadLivePodcast()
        getAllComment()

    }


    private fun loadLivePodcast() {
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getYoutubeVideoDetails(navArgs.videoid)
        }
    }

    private fun getAllComment(){
        lifecycleScope.launch {
            viewmodel.getAllComment(navArgs.pid,getLanguage())
        }
    }


    private fun initObserver()
    {
        viewmodel.podcastLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is PodcastResource.YoutubeVideoFetchFailed -> {
                    context?.toast(localContext.getString(R.string.there_is_no_live_at_the_moment))
                    onBackPress()
                }

                is PodcastResource.FetchLiveVideoUrl -> {
                    actionbar.hide()
                    exoVideoManager.playLiveVideoFromUrl(url = it.VideoUrl,isAutoPlay = true)
                    baseViewState()
                    lifecycleScope.launch {
                        viewmodel.startTask(navArgs.pid,getLanguage())
                    }
                }

                is PodcastResource.PodcastCommentLiked -> {
                    livePodcastChatAdapter.updateFav(it.copy)
                }
            }
        }

        viewmodel.podcastCommentLiveData.observe(viewLifecycleOwner){
            when(it){
                is PodcastResource.PodcastComment -> {
                    /*sendBtn.isClickable = true
                    ask_input.setText("")*/
                    chat_count.text = it.data.CommentCount.toString().numberLocale()
                    livePodcastChatAdapter.update(it.data.comments)
                }
                is PodcastResource.PodcastAddComment -> {
                    sendBtn.isClickable = true
                    ask_input.setText("")
                    chat_count.text = it.data.CommentCount.toString().numberLocale()
                    livePodcastChatAdapter.update(it.data.comments)
                }

                is CommonResource.API_CALL_FAILED -> {
                    sendBtn.isClickable = true
                    context?.toast("Failed to comment. Try again")
                }
            }
        }
    }

    private fun acquireWakeLock() {

        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun releaseWakeLock() {

        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    }


    override fun onBackPress() {

        if (exoVideoManager.isVideoPlayerFullScreen()) {
            exoVideoManager.toggleFullScreen(requireActivity())
        } else
            super.onBackPress()
    }

    override fun videoPlayerToggleFullScreen(isFullScreen: Boolean) {
        if (isFullScreen) {
            chatActionbar.hide()
            bottom_nav.hide()
        } else {
            chatActionbar.show()
            bottom_nav.show()
        }
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

    override fun livepodcastFavClicked(getdata: Comment) {

        lifecycleScope.launch {
            viewmodel.likeComment(getdata,getLanguage())
        }

    }

    override fun videoPlayerReady(isPlaying: Boolean, mediaData: CommonCardData?) {
        if(isPlaying)
            acquireWakeLock()
    }

    override fun pauseVideo(position: Int)
    {
        releaseWakeLock()
    }

}