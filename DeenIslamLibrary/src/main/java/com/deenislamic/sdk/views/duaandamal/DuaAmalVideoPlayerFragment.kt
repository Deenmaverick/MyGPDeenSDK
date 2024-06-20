package com.deenislamic.sdk.views.duaandamal

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
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
import coil.load
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.CommonCardCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.media3.ExoVideoManager
import com.deenislamic.sdk.service.libs.media3.VideoPlayerCallback
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamicEducationVideoResource
import com.deenislamic.sdk.service.models.quran.learning.QuranLearningResource
import com.deenislamic.sdk.service.network.response.common.CommonCardData
import com.deenislamic.sdk.service.repository.IslamicEducationVideoRepository
import com.deenislamic.sdk.service.repository.quran.learning.QuranLearningRepository
import com.deenislamic.sdk.utils.*
import com.deenislamic.sdk.viewmodels.IslamicEducationViewModel
import com.deenislamic.sdk.viewmodels.quran.learning.QuranLearningViewModel
import com.deenislamic.sdk.views.adapters.common.CommonCardAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch
import java.lang.Integer.min
import java.lang.Math.max

internal class DuaAmalVideoPlayerFragment : BaseRegularFragment(), VideoPlayerCallback,
    CommonCardCallback {

    private var videoList: ArrayList<CommonCardData> = arrayListOf()
    private var commonCardData: ArrayList<CommonCardData> = arrayListOf()
    private var position = 0
    private var lastPostedPlayedID = -1
        private val navArgs: DuaAmalVideoPlayerFragmentArgs by navArgs()
    private lateinit var podcastList: RecyclerView
    private lateinit var playerView: PlayerView
    private lateinit var actionbar: ConstraintLayout
    private lateinit var autoPlaySwitch:SwitchMaterial
    private lateinit var settingsLayout: LinearLayout
    private lateinit var autplayLayout:LinearLayout

    private lateinit var podcastViewTypeBtn: LinearLayout
    private lateinit var ic_viewType: AppCompatImageView
    private lateinit var optionViewtypeTxt: AppCompatTextView
    private var listItem: CommonCardData? = null
    private lateinit var commonCardAdapter: CommonCardAdapter

    private var isListView = false

    // player control

    private lateinit var vPlayerControlBtnBack: AppCompatImageView
    private lateinit var vPlayerControlAction1: AppCompatImageView
    private lateinit var vPlayerControlAction2: AppCompatImageView

    private lateinit var vPlayerControlBtnPlay: AppCompatImageView

    private lateinit var exoVideoManager: ExoVideoManager
    private lateinit var tvStoryName: AppCompatTextView
    private lateinit var viewmodel: IslamicEducationViewModel
    private lateinit var viewmodelQuranLearning: QuranLearningViewModel
    private lateinit var morePodcastTxt: AppCompatTextView
    private lateinit var title: AppCompatTextView
    private lateinit var view1: LinearLayout

    override fun OnCreate() {
        super.OnCreate()
        CallBackProvider.setFragment(this)
        // init viewmodel
        val repository = IslamicEducationVideoRepository(NetworkProvider().getInstance().provideDeenService())
        viewmodel = IslamicEducationViewModel(repository)
        viewmodelQuranLearning =QuranLearningViewModel(
            QuranLearningRepository(
            quranShikkhaService = NetworkProvider().getInstance().provideQuranShikkhaService(),
            deenService = NetworkProvider().getInstance().provideDeenService(),
            dashboardService = NetworkProvider().getInstance().provideDashboardService()
        )
        )
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainview =
            localInflater.inflate(R.layout.fragment_education_video_player, container, false)

        //init view
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        podcastList = mainview.findViewById(R.id.videoList)
        playerView = mainview.findViewById(R.id.playerView)
        actionbar = mainview.findViewById(R.id.actionbar)
        tvStoryName = mainview.findViewById(R.id.tvStoryName)
        autoPlaySwitch = mainview.findViewById(R.id.autoPlaySwitch)

        settingsLayout = mainview.findViewById(R.id.view2)
        autplayLayout = mainview.findViewById(R.id.view1)
        podcastViewTypeBtn = actionbar.findViewById(R.id.view3)
        ic_viewType = actionbar.findViewById(R.id.ic_viewType)
        optionViewtypeTxt = actionbar.findViewById(R.id.optionViewtypeTxt)

        vPlayerControlBtnBack = mainview.findViewById(R.id.vPlayerControlBtnBack)
        vPlayerControlAction1 = mainview.findViewById(R.id.vPlayerControlAction1)
        vPlayerControlAction2 = mainview.findViewById(R.id.vPlayerControlAction2)
        vPlayerControlBtnPlay = mainview.findViewById(R.id.vPlayerControlBtnPlay)
        view1 = mainview.findViewById(R.id.view1)
        morePodcastTxt = mainview.findViewById(R.id.morePodcastTxt)
        title = view1.findViewById(R.id.autoPlaytitle)

        morePodcastTxt.hide()
        vPlayerControlAction1.hide()
        settingsLayout.hide()
        autplayLayout.hide()
        setupCommonLayout(mainview)
        exoVideoManager = ExoVideoManager(
            context = requireContext(),
            isLiveVideo = false,
            mainview = mainview
        )
        exoVideoManager.setupActionbar(isBackBtn = true, title = "")

        return mainview
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackPressCallback(this)


        title.text = localContext.getString(R.string.more_videos)
        title.typeface = Typeface.DEFAULT_BOLD
        title.setTextColor(ContextCompat.getColor(requireContext(), R.color.deen_txt_black_deep))
        title.setStarMargin(10.dp)
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)


        /*if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }
        else*/
            loadpage()

    }


    private fun loadpage()
    {
        videoList = ArrayList(navArgs.videoList.toList())
        position = navArgs.videoPosition

        vPlayerControlBtnBack.setOnClickListener {
            onBackPress()
        }

        vPlayerControlAction2.setOnClickListener {
            exoVideoManager.toggleFullScreen(requireActivity())

        }


        autoPlaySwitch.setOnCheckedChangeListener { _, isChecked ->
            exoVideoManager.setAutoPlayVideoList(isChecked)
        }

        podcastList.apply {
            commonCardData = ArrayList(videoList.map { transformVideoData(it) })
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

            adapter = commonCardAdapter
            isNestedScrollingEnabled = false
            post {
                baseViewState()
            }
        }

        initObserver()

        listItem = videoList[position]
        setVideoData(listItem)


        podcastViewTypeBtn.setOnClickListener {
            commonCardAdapter.updateView(!isListView)
            updatePodcastAdapterVisibleItems()
            isListView = !isListView
            updateViewStyleOption()
        }
    }


    private fun setVideoData(listItem: CommonCardData?) {


       /* tvStoryName.text = videoList[position].title
        videoList[position].title?.let { exoVideoManager.setupActionbar(isBackBtn = true, title = it) }
        // urlvideo = BASE_CONTENT_URL_SGP + listItem.videourl

        exoVideoManager.playRegularVideoFromUrl(
            videoList = videoList,
            initialPlayerStart = true,
            isVideoAutoPlay = false
        )
        playerView.player?.seekTo(position, 0L)

        // }*/

        videoList[position].title?.let { exoVideoManager.setupActionbar(isBackBtn = true, title = it) }

        tvStoryName.text = videoList[position].title

        lifecycleScope.launch {
            listItem?.videourl?.let { viewmodelQuranLearning.getDigitalQuranSecureUrl(it) }
        }

    }

    private fun initObserver() {
        viewmodel.addHistoryLiveData.observe(viewLifecycleOwner)
        {
            when (it) {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is IslamicEducationVideoResource.addHistoryDone -> {
                    Log.e("addHistoryDone", "called$it")
                }
            }
        }

        viewmodelQuranLearning.quranLearningLiveData.observe(viewLifecycleOwner)
        {

            lifecycleScope.launch {
                viewmodelQuranLearning.clear()
            }

            when (it) {
                CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is QuranLearningResource.QuranClassSecureUrl -> {

                    exoVideoManager.playRegularVideoFromUrl(
                        singleVideoUrl = it.value,
                        initialPlayerStart = true,
                        mediaID = listItem?.Id.toString()
                    )
                }
            }
        }
    }


    private fun updatePodcastAdapterVisibleItems() {
        val layoutManager = podcastList.layoutManager as LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

        // Expanding the range by a factor (e.g., 2 more items on either side)
        val start = max(firstVisiblePosition - 4, 0)
        val end = min(lastVisiblePosition + 4, commonCardAdapter.itemCount - 1)

        val itemCount = end - start + 1

        commonCardAdapter.notifyItemRangeChanged(start, itemCount)
    }


    private fun updateViewStyleOption() {
        if (!isListView) {
            optionViewtypeTxt.text = localContext.getString(R.string.list_view)
            ic_viewType.load(R.drawable.deen_ic_list_view)
        } else {
            optionViewtypeTxt.text = localContext.getString(R.string.grid_view)
            ic_viewType.load(R.drawable.deen_ic_grid_view)
        }
    }



    override fun onBackPress() {

        if (exoVideoManager.isVideoPlayerFullScreen()) {
            exoVideoManager.toggleFullScreen(requireActivity())
        } else
            super.onBackPress()
    }

    override fun videoPlayerToggleFullScreen(isFullScreen: Boolean) {
        if (isFullScreen) {
            actionbar.hide()
            vPlayerControlAction1.hide()
        } else {
            actionbar.show()
            vPlayerControlAction1.hide()
        }

    }

    override fun videoPlayerReady(isPlaying: Boolean, mediaData: CommonCardData?) {

      if (isPlaying) {
          acquireWakeLock()
          listItem?.let {

              val newData = it.copy(isPlaying = true)
              commonCardAdapter.updatePlayPauseIcon(newData)
          }
        } else {
          listItem?.let {
              val newData = it.copy(isPlaying = false)
              commonCardAdapter.updatePlayPauseIcon(newData)
          }
          releaseWakeLock()

        }

        mediaData?.let {
            if(tvStoryName.text != it.title)
            tvStoryName.text = it.title
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        exoVideoManager.onDestory()
    }



    override fun onPause() {
        super.onPause()
        exoVideoManager.playPauseVideo()
        releaseWakeLock()
    }


    override fun onResume() {
        super.onResume()
        exoVideoManager.playPauseVideo()
    }

    private fun acquireWakeLock() {

        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun releaseWakeLock() {

        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    }

    private fun transformVideoData(newDataModel: CommonCardData): CommonCardData {

        return CommonCardData(
            Id = newDataModel.Id,
            imageurl = newDataModel.imageurl,
            title = newDataModel.title,
            videourl = newDataModel.videourl
        )
    }

    override fun commonCardClicked(getData: CommonCardData, absoluteAdapterPosition: Int) {
        if (exoVideoManager.getExoPlayer().currentMediaItem?.mediaId == getData.Id.toString()) {
            exoVideoManager.playPauseVideo()
        } else {
            position = absoluteAdapterPosition
            listItem = getData
            setVideoData(getData)
        }
    }

}