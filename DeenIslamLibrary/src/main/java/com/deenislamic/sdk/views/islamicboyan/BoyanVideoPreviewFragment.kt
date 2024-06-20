package com.deenislamic.sdk.views.islamicboyan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.CommonCardCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.BoyanResource
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.network.response.common.CommonCardData
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.views.adapters.common.CommonCardAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.base.otherFagmentActionCallback
import com.deenislamic.sdk.service.network.response.boyan.videopreview.Data
import com.deenislamic.sdk.service.repository.BoyanRepository
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.viewmodels.BoyanViewModel
import com.deenislamic.sdk.views.islamicboyan.adapter.BoyanVideoClickCallback
import com.deenislamic.sdk.views.islamicboyan.adapter.BoyanVideoPreviewPagingAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.launch


internal class BoyanVideoPreviewFragment : BaseRegularFragment(), BoyanVideoClickCallback, otherFagmentActionCallback,
    CommonCardCallback {

    private lateinit var listView: RecyclerView

    private lateinit var commonCardAdapter: CommonCardAdapter

    private lateinit var boyanVideoPreviewPagingAdapter: BoyanVideoPreviewPagingAdapter
    private lateinit var last_item_loading_progress: CircularProgressIndicator

    private lateinit var viewModel: BoyanViewModel

    private val navargs: BoyanVideoPreviewFragmentArgs by navArgs()

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

        CallBackProvider.setFragment(this)

        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_boyan_video_preview, container, false)

        //init view
        listView = mainView.findViewById(R.id.listView)
        last_item_loading_progress = mainView.findViewById(R.id.last_item_loading_progress)

        setupActionForOtherFragment(0,0,null,navargs.title?.let { it }?:localContext.getString(R.string.boyan_video),true,mainView)


        //setupActionForOtherFragment(0,0,null,args.pageTitle?.let { it }?:localContext.getString(R.string.boyan_video),true, mainView)

        setupCommonLayout(mainView)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
           boyanVideoPreviewPagingAdapter = BoyanVideoPreviewPagingAdapter(this@BoyanVideoPreviewFragment)
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
    }

    private fun loadApiData(page: Int)
    {
        lifecycleScope.launch {
            if (navargs.videoType == "category"){
                viewModel.getBoyanCategoryVideoPreview(Id = navargs.id, page = page, limit = pageItemCount)
            } else if (navargs.videoType == "scholar") {
                viewModel.getBoyanVideoPreview(Id = navargs.id, page = page, limit = pageItemCount)
            }
        }
    }

    private fun initObserver() {
        if (navargs.videoType == "category"){
            viewModel.boyanCategoryVideoPreviewLiveData.observe(viewLifecycleOwner)
            { it ->
                when(it)
                {
                    is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                    is CommonResource.EMPTY -> baseEmptyState()
                    is BoyanResource.BoyanVideoData -> {

                        setupActionForOtherFragment(R.drawable.deen_ic_grid_view,0,this@BoyanVideoPreviewFragment,navargs.title?.let { it1-> it1 }?:localContext.getString(R.string.boyan_video),true, requireView())


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
        } else if (navargs.videoType == "scholar"){
            viewModel.boyanVideoPreviewLiveData.observe(viewLifecycleOwner)
            {
                when(it)
                {
                    is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                    is CommonResource.EMPTY -> baseEmptyState()
                    is BoyanResource.BoyanVideoData -> {

                        setupActionForOtherFragment(R.drawable.deen_ic_grid_view,0,this@BoyanVideoPreviewFragment,navargs.title?.let { it1-> it1 }?:localContext.getString(R.string.boyan_video),true, requireView())

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
        loadApiData(pageNo)
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

    private fun viewState()
    {
        boyanVideoPreviewPagingAdapter.update(videoPreviewData)

        listView.post {
            baseViewState()
        }
    }


    override fun videoClick(id: Int, categoryId: Int, scholarId: Int) {
        Toast.makeText(requireContext(), "id: "+id, Toast.LENGTH_SHORT).show()
    }

    override fun action1() {
        commonCardAdapter.updateView(!isListView)
        updatePodcastAdapterVisibleItems()
        isListView = !isListView
        updateViewStyleOption()
    }

    override fun action2() {

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
            setupActionForOtherFragment(R.drawable.deen_ic_grid_view,0,this@BoyanVideoPreviewFragment,navargs.title?.let { it1-> it1 }?:localContext.getString(R.string.boyan_video),true, requireView())

        } else {
            setupActionForOtherFragment(R.drawable.deen_ic_list_view,0,this@BoyanVideoPreviewFragment,navargs.title?.let { it1-> it1 }?:localContext.getString(R.string.boyan_video),true, requireView())

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

        val bundle = Bundle()
        bundle.putInt("id", navargs.id)
        bundle.putString("videoType", navargs.videoType)
        bundle.putString("videoUrl", getData.videourl)
        bundle.putString("videoName", getData.title)
        Log.d("THeVideoUrlDataDataData", "data1:"+getData.videourl)
        gotoFrag(R.id.action_global_boyanVideoFragment, bundle)
    }
}