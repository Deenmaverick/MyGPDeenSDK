package com.deenislam.sdk.views.islamicboyan

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.common.CommonCardCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.BoyanResource
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.network.response.boyan.videopreview.Data
import com.deenislam.sdk.service.network.response.common.CommonCardData
import com.deenislam.sdk.service.repository.BoyanRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.loadHtmlFromAssets
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.viewmodels.BoyanViewModel
import com.deenislam.sdk.views.adapters.common.CommonCardAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.base.otherFagmentActionCallback
import com.deenislam.sdk.views.islamicboyan.adapter.BoyanVideoClickCallback
import com.deenislam.sdk.views.islamicboyan.adapter.BoyanVideoPreviewPagingAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.launch


internal class BoyanVideoFragment : BaseRegularFragment(), BoyanVideoClickCallback, otherFagmentActionCallback,
    CommonCardCallback {

    private lateinit var podcastViewTypeBtn: LinearLayout
    private lateinit var actionbar: ConstraintLayout
    private lateinit var ic_viewType: AppCompatImageView
    private lateinit var optionViewtypeTxt: AppCompatTextView

    private lateinit var autoPlay: LinearLayout
    private lateinit var settings: LinearLayout

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

    private var isNextEnabled  = true
    private var pageNo:Int = 1
    private var pageItemCount:Int = 100
    private var totalHadithCount = 0

    private var firstload: Boolean = false

    private var isListView = false

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

        autoPlay = actionbar.findViewById(R.id.view1)
        autoPlay.visibility = View.INVISIBLE
        settings = actionbar.findViewById(R.id.view2)
        settings.visibility = View.INVISIBLE

        podcastViewTypeBtn = actionbar.findViewById(R.id.view3)
        listView = mainView.findViewById(R.id.listView)
        last_item_loading_progress = mainView.findViewById(R.id.last_item_loading_progress)

        ic_viewType = actionbar.findViewById(R.id.ic_viewType)
        optionViewtypeTxt = actionbar.findViewById(R.id.optionViewtypeTxt)

        setupCommonLayout(mainView)
        CallBackProvider.setFragment(this)

        return mainView
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoUrl = args.videoUrl

        getHtml = requireContext().loadHtmlFromAssets("youtubeplayer.html")
            .replace("#YOUTUBE_URL#", "https://www.youtube.com/embed/$videoUrl")

        val webSettings = webview.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.displayZoomControls = false
        webSettings.builtInZoomControls = false
        webSettings.allowFileAccess = true
        webSettings.setSupportZoom(false)

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

        if(firstload) {
            loadPage()
        }
        else if (!isDetached) {
            view.postDelayed({
                loadPage()
            }, 300)
        }
        else
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
        getHtml = requireContext().loadHtmlFromAssets("youtubeplayer.html")
            .replace("#YOUTUBE_URL#","https://www.youtube.com/embed/"+videoUrl)
        webview.loadDataWithBaseURL(null, getHtml, "text/html", "UTF-8", null)

    }

    override fun action1() {

    }

    override fun action2() {

    }
}