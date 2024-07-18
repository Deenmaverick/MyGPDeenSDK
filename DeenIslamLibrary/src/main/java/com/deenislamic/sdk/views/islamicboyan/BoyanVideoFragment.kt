package com.deenislamic.sdk.views.islamicboyan

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.AudioManagerBasicCallback
import com.deenislamic.sdk.service.callback.common.CommonCardCallback
import com.deenislamic.sdk.service.callback.common.YoutubeWebViewCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.libs.media3.AudioManager
import com.deenislamic.sdk.service.models.BoyanResource
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.network.response.boyan.videopreview.Data
import com.deenislamic.sdk.service.network.response.common.CommonCardData
import com.deenislamic.sdk.service.repository.BoyanRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.YouTubeJavaScriptInterface
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.enterFullScreen
import com.deenislamic.sdk.utils.exitFullScreen
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.load
import com.deenislamic.sdk.utils.loadHtmlFromAssets
import com.deenislamic.sdk.utils.setStarMargin
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.viewmodels.BoyanViewModel
import com.deenislamic.sdk.views.adapters.common.CommonCardAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.islamicboyan.adapter.BoyanVideoClickCallback
import com.deenislamic.sdk.views.islamicboyan.adapter.BoyanVideoPreviewPagingAdapter
import com.deenislamic.sdk.views.main.MainActivityDeenSDK
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch


internal class BoyanVideoFragment : BaseRegularFragment(), BoyanVideoClickCallback,
    CommonCardCallback, YoutubeWebViewCallback, AudioManagerBasicCallback {

    private lateinit var podcastViewTypeBtn: LinearLayout
    private lateinit var actionbar: ConstraintLayout
    private lateinit var ic_viewType: AppCompatImageView
    private lateinit var optionViewtypeTxt: AppCompatTextView
    private lateinit var title: AppCompatTextView
    private lateinit var view1: LinearLayout
    private lateinit var autoPlay: LinearLayout
    private lateinit var settings: LinearLayout
    private lateinit var autoPlaySwitch: SwitchMaterial
    private lateinit var listView: RecyclerView
    private lateinit var last_item_loading_progress: CircularProgressIndicator

    private lateinit var commonCardAdapter: CommonCardAdapter
    private lateinit var boyanVideoPreviewPagingAdapter: BoyanVideoPreviewPagingAdapter

    private lateinit var webview: WebView
    private lateinit var txtviwVideoName: AppCompatTextView

    private var videoUrl: String = ""
    private var getHtml: String = ""

    private val args: BoyanVideoFragmentArgs by navArgs()

    private lateinit var viewModel: BoyanViewModel

    private var videoPreviewData :ArrayList<Data> = arrayListOf()
    private var commonCardData: ArrayList<CommonCardData> = arrayListOf()
    private var currentPlayerData:CommonCardData ? = null

    private var isNextEnabled  = true
    private var pageNo:Int = 1
    private var pageItemCount:Int = 100
    private var totalHadithCount = 0

    private var firstload: Boolean = false
    private lateinit var audioManager: AudioManager
    private var isListView = false
    private lateinit var btnFullscreen: FrameLayout
    private var isFullScreen = false

    override fun OnCreate() {
        super.OnCreate()
        val boyanRepository = BoyanRepository(deenService = NetworkProvider().getInstance().provideDeenService())
        viewModel = BoyanViewModel(boyanRepository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_boyan_video, container, false)

        //init view
        webview = mainView.findViewById(R.id.playerWebView)
        txtviwVideoName = mainView.findViewById(R.id.tvStoryName)
        txtviwVideoName.text = args.videoName

        actionbar = mainView.findViewById(R.id.actionbar)
        view1 = mainView.findViewById(R.id.view1)
        title = view1.findViewById(R.id.autoPlaytitle)

        autoPlaySwitch = view1.findViewById(R.id.autoPlaySwitch)
        autoPlaySwitch.hide()
        settings = actionbar.findViewById(R.id.view2)
        settings.hide()

        podcastViewTypeBtn = actionbar.findViewById(R.id.view3)
        listView = mainView.findViewById(R.id.listView)
        last_item_loading_progress = mainView.findViewById(R.id.last_item_loading_progress)
        btnFullscreen = mainView.findViewById(R.id.btnFullscreen)
        btnFullscreen.setOnClickListener {
            onYoutubeFullscreenClicked()
        }
        ic_viewType = actionbar.findViewById(R.id.ic_viewType)
        optionViewtypeTxt = actionbar.findViewById(R.id.optionViewtypeTxt)
        val jsInterface = YouTubeJavaScriptInterface(this)
        webview.addJavascriptInterface(jsInterface, "AndroidYouTubeInterface")
        setupCommonLayout(mainView)
        CallBackProvider.setFragment(this)
        audioManager = AudioManager().getInstance()
        return mainView
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackPressCallback(this)
        title.text = localContext.getString(R.string.more_videos)
        title.typeface = Typeface.DEFAULT_BOLD
        title.setTextColor(ContextCompat.getColor(requireContext(), R.color.deen_txt_black_deep))
        title.setStarMargin(10.dp)
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)


        videoUrl = args.videoUrl

        getHtml = requireContext().loadHtmlFromAssets("youtubeplayernew.html")
            .replace("#YOUTUBE_URL#", videoUrl)

        val webSettings = webview.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.displayZoomControls = false
        webSettings.builtInZoomControls = false
        webSettings.allowFileAccess = true
        webSettings.setSupportZoom(false)
        webSettings.mediaPlaybackRequiresUserGesture = false
        webSettings.allowFileAccess = true; // Add this line
        webSettings.allowContentAccess = true; // Add this line
        webSettings.setAllowUniversalAccessFromFileURLs(true); // Add this line

        webview.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {

                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                baseViewState()
            }
        }

        webview.loadDataWithBaseURL(null, getHtml, "text/html", "UTF-8", null)

        boyanVideoPreviewPagingAdapter = BoyanVideoPreviewPagingAdapter(this@BoyanVideoFragment)

        initObserver()

        /*if(firstload) {
            loadPage()
        }
        else if (!isDetached) {
            view.postDelayed({
                loadPage()
            }, 300)
        }
        else*/
            loadPage()

        firstload = true
    }

    private fun loadPage()
    {

        baseLoadingState()

        listView.apply {
            setPadding(0,12.dp,0,0)

            overScrollMode = View.OVER_SCROLL_NEVER
            adapter = boyanVideoPreviewPagingAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        listView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1)) {
                    // RecyclerView has reached the end.
                    // Load more data here if you are implementing pagination.
                    // NestedScrollView has scrolled to the end
                    if(isNextEnabled && boyanVideoPreviewPagingAdapter.itemCount>0) {
                        fetchNextPageData()
                        morePageBottomLoading(true)
                    }
                    else
                        morePageBottomLoading(false)
                }
            }
        })

        initObserver()
        loadApiData(pageNo)

        podcastViewTypeBtn.setOnClickListener {
            commonCardAdapter.updateView(!isListView)
            updatePodcastAdapterVisibleItems()
            isListView = !isListView
            updateViewStyleOption()
        }
    }

    private fun loadApiData(page: Int)
    {
        lifecycleScope.launch {
            if (args.videoType == "category"){
                viewModel.getBoyanCategoryVideoPreview(Id = args.id, page = page, limit = pageItemCount)
            } else if (args.videoType == "scholar") {
                viewModel.getBoyanVideoPreview(Id = args.id, page = page, limit = pageItemCount)
            }
        }
    }

    private fun initObserver() {
        if (args.videoType == "category"){
            viewModel.boyanCategoryVideoPreviewLiveData.observe(viewLifecycleOwner)
            {
                when(it)
                {
                    is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                    is CommonResource.EMPTY -> baseEmptyState()
                    is BoyanResource.BoyanVideoData -> {



                        morePageBottomLoading(false)

                        totalHadithCount =  it.data.Pagination.TotalData

                        listView.apply {

                            videoPreviewData = it.data.Data as ArrayList<Data>

                            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                            commonCardData = ArrayList(videoPreviewData.map { transformVideoData(it) })

                            commonCardAdapter = CommonCardAdapter(
                                data = commonCardData,
                                isShowLiveIcon = false,
                                isShowPlayIcon = true,
                                itemMarginTop = 0.dp,
                                itemMarginLeft = 0,
                                itemMarginRight = 0,
                                itemPaddingBottom = 12.dp,
                                bannerSize = 1280
                            )

                            adapter = commonCardAdapter
                            isNestedScrollingEnabled = false

                            baseViewState()
                        }

                    }

                }
            }
        } else if (args.videoType == "scholar"){
            viewModel.boyanVideoPreviewLiveData.observe(viewLifecycleOwner)
            {
                when(it)
                {
                    is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                    is CommonResource.EMPTY -> baseEmptyState()
                    is BoyanResource.BoyanVideoData -> {

                        //Toast.makeText(requireContext(), "data"+it.data.Data, Toast.LENGTH_SHORT).show()

                        morePageBottomLoading(false)

                        totalHadithCount =  it.data.Pagination.TotalData

                        listView.apply {

                            videoPreviewData = it.data.Data as ArrayList<Data>

                            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                            commonCardData = ArrayList(videoPreviewData.map { transformVideoData(it) })

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

                            baseViewState()
                        }


                    }
                }
            }
        }

    }

    override fun noInternetRetryClicked() {
        //loadApiData(pageNo)
    }

    private fun morePageBottomLoading(bol:Boolean)
    {
        last_item_loading_progress.visible(bol)
    }

    private fun fetchNextPageData() {

        lifecycleScope.launch {
            pageNo++
            //loadApiData(pageNo)
            isNextEnabled = false
        }
    }

    override fun videoClick(id: Int, categoryId: Int, scholarId: Int) {
        Toast.makeText(requireContext(), "id: "+id, Toast.LENGTH_SHORT).show()
    }

    private fun updatePodcastAdapterVisibleItems() {
        val layoutManager = listView.layoutManager as LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

        // Expanding the range by a factor (e.g., 2 more items on either side)
        val start = Math.max(firstVisiblePosition - 4, 0)
        val end = Integer.min(lastVisiblePosition + 4, commonCardAdapter.itemCount - 1)

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

    private fun transformVideoData(newDataModel: Data): CommonCardData {

        return CommonCardData(
            Id = newDataModel.Id,
            imageurl = newDataModel.imageurl1,
            title = newDataModel.title,
            videourl = newDataModel.contenturl
        )
    }

    override fun commonCardClicked(getData: CommonCardData, absoluteAdapterPosition: Int) {
        super.commonCardClicked(getData, absoluteAdapterPosition)

        txtviwVideoName.text = getData.title

        videoUrl = getData.videourl!!
        getHtml = requireContext().loadHtmlFromAssets("youtubeplayernew.html")
            .replace("#YOUTUBE_URL#",videoUrl)
        webview.loadDataWithBaseURL(null, getHtml, "text/html", "UTF-8", null)

    }

    override fun onYoutubePlayingState(){
        Log.e("YoutubePlayer","Playing")
        MainActivityDeenSDK.instance?.pauseQuran()
        lifecycleScope.launch {
            currentPlayerData?.let {
                val newData = it.copy(isPlaying = true)
                commonCardAdapter.updatePlayPauseIcon(newData)
            }
        }

    }

    override fun onYoutubePauseState(){
        lifecycleScope.launch {
            currentPlayerData?.let {
                val newData = it.copy(isPlaying = false)
                commonCardAdapter.updatePlayPauseIcon(newData)
            }
        }
    }

    override fun onYoutubeEndedState(){
        lifecycleScope.launch {
            currentPlayerData?.let {
                val newData = it.copy(isPlaying = false)
                commonCardAdapter.updatePlayPauseIcon(newData)
            }
        }
    }

    override fun onYoutubeReadyState(){
        Log.e("YoutubePlayer","Ready")
    }

    override fun isMedia3Pause(){
        webview.evaluateJavascript("player.pauseVideo();", null);
    }

    override fun onYoutubeFullscreenClicked(){
        if (!isFullScreen) {

            // Go full-screen in landscape mode
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            val params = webview.layoutParams as ConstraintLayout.LayoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            webview.layoutParams = params
            activity?.enterFullScreen()
            isFullScreen = true

        } else {

            // Exit full-screen and revert to the original orientation
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            val params = webview.layoutParams as ConstraintLayout.LayoutParams
            params.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            params.height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT  // Adjust the height as needed
            webview.layoutParams = params
            activity?.exitFullScreen()
            isFullScreen = false

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webview.let {
            it.clearHistory();
            it.clearCache(true);
            it.loadUrl("about:blank");
            it.onPause();
            it.removeAllViews();
            it.destroy();
        }
    }
}