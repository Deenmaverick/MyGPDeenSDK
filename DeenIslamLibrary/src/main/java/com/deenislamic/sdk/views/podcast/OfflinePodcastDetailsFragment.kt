package com.deenislamic.sdk.views.podcast

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.CommonCardCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.media3.ExoVideoManager
import com.deenislamic.sdk.service.libs.media3.VideoPlayerCallback
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.PodcastResource
import com.deenislamic.sdk.service.network.response.common.CommonCardData
import com.deenislamic.sdk.service.network.response.podcast.content.Content
import com.deenislamic.sdk.service.repository.PodcastRepository
import com.deenislamic.sdk.service.repository.YoutubeVideoRepository
import com.deenislamic.sdk.service.repository.quran.learning.QuranLearningRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.load
import com.deenislamic.sdk.utils.setStarMargin
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.viewmodels.PodcastViewModel
import com.deenislamic.sdk.views.adapters.common.CommonCardAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch
import java.lang.Integer.min
import java.lang.Math.max


internal class OfflinePodcastDetailsFragment : BaseRegularFragment(), VideoPlayerCallback,
    CommonCardCallback {

    private var commonCardData: ArrayList<CommonCardData> = arrayListOf()
    private var listItem: CommonCardData? = null
    private lateinit var podcastList: RecyclerView
    private lateinit var playerView: PlayerView
    private lateinit var pdactionbar:ConstraintLayout
    private lateinit var actionbar:ConstraintLayout
    private lateinit var tvStoryName:AppCompatTextView
    private lateinit var podcastViewTypeBtn:LinearLayout
    private lateinit var ic_viewType:AppCompatImageView
    private lateinit var autoPlaySwitch: SwitchMaterial
    private lateinit var autoPlaytitle:AppCompatTextView
    private lateinit var optionViewtypeTxt:AppCompatTextView
    private lateinit var commonCardAdapter: CommonCardAdapter
    private lateinit var settingsLayout: LinearLayout
    private lateinit var vPlayerControlAction1: AppCompatImageView
    private var isListView = false
    private var lastPostedPlayedID = -1
    // player control

    private lateinit var vPlayerControlAction2: AppCompatImageView
    private lateinit var vPlayerControlBtnBack: AppCompatImageView

    private lateinit var vPlayerControlBtnPlay: AppCompatImageView

    private lateinit var exoVideoManager: ExoVideoManager

    private lateinit var viewmodel: PodcastViewModel
    private val navArgs:OfflinePodcastDetailsFragmentArgs by navArgs()


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

        val mainview = localInflater.inflate(R.layout.fragment_offline_podcast_details,container,false)

        //init view

        podcastList = mainview.findViewById(R.id.videoList)
        playerView = mainview.findViewById(R.id.playerView)
        pdactionbar = mainview.findViewById(R.id.pdactionbar)
        settingsLayout = mainview.findViewById(R.id.view2)
        podcastViewTypeBtn = pdactionbar.findViewById(R.id.view3)
        ic_viewType = pdactionbar.findViewById(R.id.ic_viewType)
        optionViewtypeTxt = pdactionbar.findViewById(R.id.optionViewtypeTxt)
        tvStoryName = mainview.findViewById(R.id.tvStoryName)
        autoPlaytitle = mainview.findViewById(R.id.autoPlaytitle)
        vPlayerControlBtnBack = mainview.findViewById(R.id.vPlayerControlBtnBack)
        vPlayerControlBtnPlay = mainview.findViewById(R.id.vPlayerControlBtnPlay)
        vPlayerControlAction2 = mainview.findViewById(R.id.vPlayerControlAction2)
        vPlayerControlAction1 = mainview.findViewById(R.id.vPlayerControlAction1)
        actionbar = mainview.findViewById(R.id.actionbar)
        autoPlaySwitch = mainview.findViewById(R.id.autoPlaySwitch)
        settingsLayout.hide()
        vPlayerControlAction1.hide()
        autoPlaySwitch.hide()
        exoVideoManager = ExoVideoManager(
            context = requireContext(),
            isLiveVideo = false,
            mainview = mainview
        )
        exoVideoManager.setupActionbar(isBackBtn = true, title = localContext.getString(R.string.podcast))

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.podcast),
            backEnable = true,
            view = mainview
        )

        setupCommonLayout(mainview)

        return mainview
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackPressCallback(this)

        /*if (!isDetached) {
            view.postDelayed({
                loadPage()
            }, 300)
        }
        else*/
            loadPage()


    }



    private fun loadPage(){

        autoPlaytitle.text = localContext.getString(R.string.watch_more)
        autoPlaytitle.typeface = Typeface.DEFAULT_BOLD
        autoPlaytitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.deen_txt_black_deep))
        autoPlaytitle.setStarMargin(10.dp)
        autoPlaytitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)


        autoPlaySwitch.setOnCheckedChangeListener { _, isChecked ->
            exoVideoManager.setAutoPlayVideoList(isChecked)
        }

        vPlayerControlAction2.setOnClickListener {
            exoVideoManager.toggleFullScreen(requireActivity())

        }

        vPlayerControlBtnBack.setOnClickListener {
            onBackPress()
        }


        podcastViewTypeBtn.setOnClickListener {
            commonCardAdapter.updateView(!isListView)
            updatePodcastAdapterVisibleItems()
            isListView = !isListView
            updateViewStyleOption()
        }


        initObserver()

        loadapi()

    }


    private fun initObserver(){
        viewmodel.podcastLiveData.observe(viewLifecycleOwner){
            when(it){
                CommonResource.EMPTY -> baseEmptyState()
                CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is PodcastResource.PodcastContent ->{
                    tvStoryName.text = it.data.Title

                    commonCardData = ArrayList(it.data.Contents.map { transformVideoData(it) })

                    commonCardAdapter = CommonCardAdapter(
                        data = commonCardData,
                        isShowLiveIcon = false,
                        isShowPlayIcon = true,
                        itemMarginTop = 8.dp,
                        itemMarginLeft = 0,
                        itemMarginRight = 0,
                        itemPaddingBottom = 12.dp,
                        bannerSize = 1280
                    )

                    podcastList.apply {
                        adapter = commonCardAdapter
                        isNestedScrollingEnabled = false
                    }

                    exoVideoManager.setupActionbar(isBackBtn = true, title = it.data.Title)

                    if (it.data.Contents.getOrNull(0) != null) {
                        lifecycleScope.launch {
                            listItem = commonCardData[0]
                            viewmodel.secureUrl(it.data.Contents[0].RefUrl)
                        }
                    }
                    else
                        baseNoInternetState()
                }
            }
        }

        viewmodel.secureUrlLiveData.observe(viewLifecycleOwner)
        {

            when(it)
            {
                CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is PodcastResource.SecureUrl -> {
                    actionbar.hide()
                    exoVideoManager.playRegularVideoFromUrl(
                        singleVideoUrl = it.value,
                        initialPlayerStart = true,
                        startFromDuration =  listItem?.DurationWatched?:0,
                        mediaID = listItem?.Id.toString()
                    )

                    baseViewState()
                }

            }
        }
    }

    override fun noInternetRetryClicked() {
        baseLoadingState()
        loadapi()
    }

    private fun loadapi(){
        lifecycleScope.launch {
            viewmodel.getPodcastContent(getLanguage(),navArgs.pid)
        }
    }



    override fun onBackPress() {

        if (exoVideoManager.isVideoPlayerFullScreen()) {
            exoVideoManager.toggleFullScreen(requireActivity())
        } else
            super.onBackPress()
    }

    private fun updatePodcastAdapterVisibleItems() {
        val layoutManager = podcastList.layoutManager as LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

        // Expanding the range by a factor (e.g., 2 more items on either side)
        val start = max(firstVisiblePosition - 3, 0)
        val end = min(lastVisiblePosition + 3, commonCardAdapter.itemCount - 1)

        val itemCount = end - start + 1

        commonCardAdapter.notifyItemRangeChanged(start, itemCount)
    }


    private fun updateViewStyleOption()
    {
        if(!isListView)
        {
            optionViewtypeTxt.text = localContext.getString(R.string.list_view)
            ic_viewType.load(R.drawable.deen_ic_list_view)
        }
        else
        {
            optionViewtypeTxt.text = localContext.getString(R.string.grid_view)
            ic_viewType.load(R.drawable.deen_ic_grid_view)
        }
    }

    override fun videoPlayerToggleFullScreen(isFullScreen: Boolean) {
        if (isFullScreen) {
            pdactionbar.hide()
        } else {
            pdactionbar.show()
        }
    }


    private fun acquireWakeLock() {

        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun releaseWakeLock() {

        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

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
        if (isPlaying) {
            acquireWakeLock()
            listItem?.let {

                val newData = it.copy(isPlaying = true)
                commonCardAdapter.updatePlayPauseIcon(newData)
            }
        } else {
            releaseWakeLock()
            listItem?.let {
                val newData = it.copy(isPlaying = false)
                commonCardAdapter.updatePlayPauseIcon(newData)
            }

        }
    }

    private fun transformVideoData(newDataModel: Content): CommonCardData {

        return CommonCardData(
            Id = newDataModel.Id,
            imageurl = newDataModel.ImageUrl,
            title = newDataModel.Title,
            videourl = newDataModel.RefUrl
        )
    }


    override fun commonCardClicked(getData: CommonCardData, absoluteAdapterPosition: Int) {
        if (exoVideoManager.getExoPlayer().currentMediaItem?.mediaId == getData.Id.toString()) {
            exoVideoManager.playPauseVideo()
        } else {
            listItem = getData
            setVideoData(getData)
        }
    }

    private fun setVideoData(listItem: CommonCardData?) {

        lifecycleScope.launch {
            listItem?.videourl?.let {
                tvStoryName.text = listItem.title
                viewmodel.secureUrl(it) }
        }

    }

}