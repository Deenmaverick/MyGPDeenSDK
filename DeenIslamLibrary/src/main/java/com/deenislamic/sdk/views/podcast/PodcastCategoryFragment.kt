package com.deenislamic.sdk.views.podcast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.CommonCardCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.PodcastResource
import com.deenislamic.sdk.service.network.response.common.CommonCardData
import com.deenislamic.sdk.service.repository.PodcastRepository
import com.deenislamic.sdk.service.repository.YoutubeVideoRepository
import com.deenislamic.sdk.service.repository.quran.learning.QuranLearningRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.viewmodels.PodcastViewModel
import com.deenislamic.sdk.views.adapters.common.CommonCardAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.base.otherFagmentActionCallback
import kotlinx.coroutines.launch


internal class PodcastCategoryFragment : BaseRegularFragment(),otherFagmentActionCallback,
    CommonCardCallback {

    private lateinit var listView: RecyclerView

    private lateinit var commonCardAdapter: CommonCardAdapter

    private lateinit var viewModel: PodcastViewModel
    private val navargs:PodcastCategoryFragmentArgs by navArgs()

    private var commonCardData: ArrayList<CommonCardData> = arrayListOf()

    private var firstload: Boolean = false

    private var isListView = false

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

        viewModel = PodcastViewModel(
            youtubeVideoRepository = youtubeVideoRepository,
            podcastRepository = podcastRepository,
            quranLearningRepository = quranLearningRepository
        )
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

        setupActionForOtherFragment(0,0,null, navargs.title,true,mainView)

        setupCommonLayout(mainView)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*if (!isDetached) {
            view.postDelayed({
                loadPage()
            }, 300)
        }
        else*/
            loadPage()

    }

    private fun loadPage()
    {

        initObserver()

        if(!firstload)
        loadApiData()
        firstload = true
    }

    private fun loadApiData()
    {
        lifecycleScope.launch {
            viewModel.getPodcastCat(getLanguage(),navargs.cid)
        }
    }

    private fun initObserver() {

        viewModel.podcastLiveData.observe(viewLifecycleOwner){
            when(it){
                CommonResource.API_CALL_FAILED -> baseNoInternetState()
                CommonResource.EMPTY -> baseEmptyState()
                is PodcastResource.PodcastCat -> {

                    listView.apply {


                        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        commonCardData = ArrayList(it.data.map { cdata -> transformVideoData(cdata) })

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

    override fun noInternetRetryClicked() {
        loadApiData()
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
            setupActionForOtherFragment(R.drawable.deen_ic_grid_view,0,this,
                navargs.title,true, requireView())

        } else {
            setupActionForOtherFragment(R.drawable.deen_ic_list_view,0,this,
                navargs.title,true, requireView())

        }
    }

    private fun transformVideoData(newDataModel: com.deenislamic.sdk.service.network.response.podcast.category.Data): CommonCardData {

        return CommonCardData(
            Id = newDataModel.Id,
            imageurl = newDataModel.ImageUrl,
            title = newDataModel.Title,
            videourl = newDataModel.RefUrl,
            isLive = newDataModel.IsLive

        )
    }

    override fun commonCardClicked(getData: CommonCardData, absoluteAdapterPosition: Int) {

        if(!Subscription.isSubscribe){
            gotoFrag(R.id.action_global_subscriptionFragment)
            return
        }
        if(getData.isLive) {
            if(getData.videourl?.isNotEmpty() == true) {
                val bundle = Bundle()
                bundle.putString("videoid",getData.videourl)
                bundle.putString("title",getData.title)
                bundle.putInt("pid",getData.Id)
                gotoFrag(R.id.action_global_livePodcastDetailsFragment,bundle)
            }else
                context?.let { it.toast(localContext.getString(R.string.there_is_no_live_at_the_moment)) }
        }
        else {
            val bundle = Bundle()
            bundle.putInt("pid",getData.Id)
            gotoFrag(R.id.action_global_offlinePodcastDetailsFragment,bundle)
        }
    }

}