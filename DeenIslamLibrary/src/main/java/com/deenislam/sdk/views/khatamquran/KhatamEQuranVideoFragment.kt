package com.deenislam.sdk.views.khatamquran

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.common.CommonCardCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.libs.media3.ExoVideoManager
import com.deenislam.sdk.service.libs.media3.VideoPlayerCallback
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.IslamicEducationVideoResource
import com.deenislam.sdk.service.models.quran.learning.QuranLearningResource
import com.deenislam.sdk.service.network.response.common.CommonCardData
import com.deenislam.sdk.service.repository.KhatamEquranVideoRepository
import com.deenislam.sdk.service.repository.quran.learning.QuranLearningRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.setStarMargin
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.viewmodels.KhatamQuranViewModel
import com.deenislam.sdk.viewmodels.quran.learning.QuranLearningViewModel
import com.deenislam.sdk.views.adapters.common.CommonCardAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch
import java.lang.Integer.min
import java.lang.Math.max


internal class KhatamEQuranVideoFragment : BaseRegularFragment(), VideoPlayerCallback,
    CommonCardCallback {

    private var videoList: ArrayList<CommonCardData> = arrayListOf()
    private var commonCardData: ArrayList<CommonCardData> = arrayListOf()
    private var position = 0
    private val navArgs: KhatamEQuranVideoFragmentArgs by navArgs()
    private lateinit var podcastList: RecyclerView
    private lateinit var playerView: PlayerView
    private lateinit var actionbar: ConstraintLayout
    private lateinit var autoPlaySwitch: SwitchMaterial
    private lateinit var settingsLayout: LinearLayout

    private lateinit var podcastViewTypeBtn: LinearLayout
    private lateinit var ic_viewType: AppCompatImageView
    private lateinit var optionViewtypeTxt: AppCompatTextView

    private lateinit var commonCardAdapter: CommonCardAdapter

    private var isListView = false

    // player control

    private lateinit var vPlayerControlBtnBack: AppCompatImageView
    private lateinit var vPlayerControlAction1: AppCompatImageView
    private lateinit var vPlayerControlAction2: AppCompatImageView
    private lateinit var exo_prev: AppCompatImageView
    private lateinit var exo_next: AppCompatImageView
    private lateinit var view1: LinearLayout
    private lateinit var title: AppCompatTextView

    private lateinit var vPlayerControlBtnPlay: AppCompatImageView
    private lateinit var exoVideoManager: ExoVideoManager
    private lateinit var tvStoryName: AppCompatTextView
    private lateinit var viewmodel:KhatamQuranViewModel
    private lateinit var viewmodelQuranLearning:QuranLearningViewModel
    private var listItem: CommonCardData? = null

    private lateinit var customDialognumberInputView: View
    private var paraCompleteDialog: AlertDialog? = null
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var morePodcastTxt: AppCompatTextView

    override fun OnCreate() {
        super.OnCreate()
        // init viewmodel
        val repository = KhatamEquranVideoRepository(
            deenService = NetworkProvider().getInstance().provideDeenService())
        viewmodel = KhatamQuranViewModel(repository)
        viewmodelQuranLearning =QuranLearningViewModel(QuranLearningRepository(
            quranShikkhaService = NetworkProvider().getInstance().provideQuranShikkhaService(),
            deenService = NetworkProvider().getInstance().provideDeenService(),
            dashboardService = NetworkProvider().getInstance().provideDashboardService()
        ))
        CallBackProvider.setFragment(this)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainview =
            localInflater.inflate(R.layout.fragment_education_video_player, container, false)

        //init view
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        materialAlertDialogBuilder =
            MaterialAlertDialogBuilder(requireContext(), R.style.DeenMaterialAlertDialog_rounded)
        customDialognumberInputView =
            localInflater.inflate(R.layout.dialog_khatam_quran_para_comlete, null, false)

        podcastList = mainview.findViewById(R.id.videoList)
        playerView = mainview.findViewById(R.id.playerView)
        actionbar = mainview.findViewById(R.id.actionbar)
        tvStoryName = mainview.findViewById(R.id.tvStoryName)
        autoPlaySwitch = mainview.findViewById(R.id.autoPlaySwitch)
        morePodcastTxt = mainview.findViewById(R.id.morePodcastTxt)

        view1 = mainview.findViewById(R.id.view1)
        title = view1.findViewById(R.id.autoPlaytitle)

        settingsLayout = mainview.findViewById(R.id.view2)

        podcastViewTypeBtn = actionbar.findViewById(R.id.view3)
        ic_viewType = actionbar.findViewById(R.id.ic_viewType)
        optionViewtypeTxt = actionbar.findViewById(R.id.optionViewtypeTxt)

        vPlayerControlBtnBack = mainview.findViewById(R.id.vPlayerControlBtnBack)
        vPlayerControlAction1 = mainview.findViewById(R.id.vPlayerControlAction1)
        vPlayerControlAction2 = mainview.findViewById(R.id.vPlayerControlAction2)
        vPlayerControlBtnPlay = mainview.findViewById(R.id.vPlayerControlBtnPlay)
        exo_prev = mainview.findViewById(R.id.exo_prev)
        exo_next = mainview.findViewById(R.id.exo_next)
        vPlayerControlAction1.hide()
        settingsLayout.hide()
        autoPlaySwitch.hide()
        morePodcastTxt.hide()
        playerView.setShowNextButton(false)
        playerView.setShowPreviousButton(false)

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
        } else*/
            loadpage()

    }

    private fun loadpage() {
        videoList = ArrayList(navArgs.khatamQuranvideoList.toList())
        position = navArgs.khatamQuranvideoPosition

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

        videoList[position].title?.let { exoVideoManager.setupActionbar(isBackBtn = true, title = it) }

        tvStoryName.text = videoList[position].title

        lifecycleScope.launch {
            viewmodelQuranLearning.getDigitalQuranSecureUrl(listItem?.videourl!!)
        }

    }

    private fun initObserver() {
        viewmodel.addHistoryLiveData.observe(viewLifecycleOwner)
        {
            when (it) {
                is CommonResource.API_CALL_FAILED -> Log.e("addHistoryDone", "API_CALL_FAILED")
                is CommonResource.EMPTY -> Log.e("addHistoryDone", "EMPTY")
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
                        startFromDuration =  listItem?.DurationWatched?:0,
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
        submitWatchDuration(listItem)
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

    override fun videoPlayerEnded() {

        if (position < videoList.size) {
            showParaCompleteDialog("আপনি ${listItem?.reference} সম্পন্ন করেছেন। পরবর্তী পারা শুরু করুন।")
            position++
        }
    }

    private fun submitWatchDuration(listItem: CommonCardData?) {

        if(navArgs.isRamadan)
            return

            if (!(listItem?.IsCompleted!!)) {
                lifecycleScope.launch {
                        viewmodel.postKhatamQuranHistory(
                            contentID = listItem.Id,
                            totalDuration = listItem.DurationInSec,
                            duration = (exoVideoManager.getExoPlayer().currentPosition / 1000).toInt(),
                            language = getLanguage()
                        )

                }
            }

    }


    override fun videoPlayerReady(isPlaying: Boolean, mediaData: CommonCardData?) {
        if (isPlaying) {
            listItem?.let {

                submitWatchDuration(it)

                val newData = it.copy(isPlaying = true)
                commonCardAdapter.updatePlayPauseIcon(newData)
            }
        } else {
            listItem?.let {
                val newData = it.copy(isPlaying = false)
                commonCardAdapter.updatePlayPauseIcon(newData)
            }

        }
    }




    override fun onDestroyView() {
        super.onDestroyView()

        submitWatchDuration(listItem)

        exoVideoManager.onDestory()
    }


    override fun onPause() {

        Log.e("onPause", "Called")
        exoVideoManager.playPauseVideo()
        submitWatchDuration(listItem)

        super.onPause()
    }


    override fun onResume() {

        Log.e("onResume", "Called")
        exoVideoManager.playPauseVideo()

        super.onResume()
    }


    private fun transformVideoData(newDataModel: CommonCardData): CommonCardData {

        return CommonCardData(
            Id = newDataModel.Id,
            imageurl = newDataModel.imageurl,
            title = newDataModel.title,
            videourl = newDataModel.videourl,
            reference = null,
            customReference = newDataModel.reference
        )
    }

    private fun showParaCompleteDialog(paraNumber: String?) {

        val hint: AppCompatTextView = customDialognumberInputView.findViewById(R.id.hint)
        val btnNextPara: MaterialButton = customDialognumberInputView.findViewById(R.id.nextParaBtn)

        if (paraCompleteDialog?.isShowing == true)
            paraCompleteDialog?.dismiss()

        if (customDialognumberInputView.parent != null)
            (customDialognumberInputView.parent as ViewGroup).removeView(
                customDialognumberInputView
            )


        paraCompleteDialog = materialAlertDialogBuilder
            .setView(customDialognumberInputView)
            .setCancelable(false)
            .show().apply {
                window?.setGravity(Gravity.CENTER)
            }

        hint.text = paraNumber

        btnNextPara.setOnClickListener {
            paraCompleteDialog?.dismiss()
            paraCompleteDialog = null
            listItem = videoList.get(position)
            setVideoData(listItem)
        }

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