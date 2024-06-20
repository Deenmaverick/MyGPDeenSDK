package com.deenislamic.sdk.views.podcast

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.AdvertisementCallback
import com.deenislamic.sdk.service.callback.LivePodcastCallback
import com.deenislamic.sdk.service.callback.common.HorizontalCardListCallback
import com.deenislamic.sdk.service.callback.common.PatchCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.PodcastResource
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.service.repository.PodcastRepository
import com.deenislamic.sdk.service.repository.YoutubeVideoRepository
import com.deenislamic.sdk.service.repository.quran.learning.QuranLearningRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.Subscription
import com.deenislamic.sdk.utils.get9DigitRandom
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.viewmodels.PodcastViewModel
import com.deenislamic.sdk.views.adapters.podcast.LivePodcastMainAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


internal class LivePodcastFragment : BaseRegularFragment(), LivePodcastCallback,
    HorizontalCardListCallback, AdvertisementCallback, PatchCallback {

    private lateinit var listMain:RecyclerView
    private lateinit var viewmodel: PodcastViewModel
    private lateinit var livePodcastMainAdapter: LivePodcastMainAdapter
    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)

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

        val mainview = localInflater.inflate(R.layout.fragment_live_podcast,container,false)

        //init view
        listMain = mainview.findViewById(R.id.listMain)


        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.live_podcast),
            backEnable = true,
            view = mainview
        )

        setupCommonLayout(mainview)
        CallBackProvider.setFragment(this)

        return mainview
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!firstload)
        {
            lifecycleScope.launch {
                setTrackingID(get9DigitRandom())
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "podcast",
                    trackingID = getTrackingID()
                )
            }
        }


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

        livePodcastMainAdapter = LivePodcastMainAdapter()

        listMain.apply {
            adapter = livePodcastMainAdapter
            isNestedScrollingEnabled = false
        }

        initObserver()

        if (!firstload)
        loadapi()
        firstload = true
    }

    private fun initObserver(){

        viewmodel.podcastLiveData.observe(viewLifecycleOwner){
            when(it){
                CommonResource.API_CALL_FAILED -> baseNoInternetState()
                CommonResource.EMPTY -> baseEmptyState()
                is PodcastResource.HomePatch ->{
                    livePodcastMainAdapter.update(it.data)
                    baseViewState()
                }
            }
        }
    }

    private fun loadapi(){

        lifecycleScope.launch {
            viewmodel.getPodcastHomePatch(getLanguage())
        }
    }

    override fun noInternetRetryClicked() {
        baseLoadingState()
        loadapi()
    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "podcast",
                    trackingID = getTrackingID()
                )
            }
        }

        tryCatch { super.onBackPress() }

    }

    override fun podcastItemClicked() {
        gotoFrag(R.id.livePodcastDetailsFragment)
    }

    override fun offlinePodcastItemClicked() {
        gotoFrag(R.id.offlinePodcastDetailsFragment)
    }

    override fun patchItemClicked(getData: Item) {
        when(getData.ContentType){
            "ipd" -> {
                if(getData.isLive) {
                    if(getData.imageurl2.isNotEmpty()) {
                        val bundle = Bundle()
                        bundle.putString("videoid",getData.imageurl2)
                        bundle.putInt("pid",getData.Id)
                        bundle.putString("title",getData.ArabicText)
                        gotoFrag(R.id.action_global_livePodcastDetailsFragment,bundle)
                    }else
                        context?.let { it.toast(localContext.getString(R.string.there_is_no_live_at_the_moment)) }
                }
                else {
                    if(!Subscription.isSubscribe){
                        gotoFrag(R.id.action_global_subscriptionFragment)
                        return
                    }
                    val bundle = Bundle()
                    bundle.putInt("pid",getData.Id)
                    gotoFrag(R.id.action_global_offlinePodcastDetailsFragment,bundle)
                }
            }

            "ipl" -> {
                val bundle = Bundle()
                bundle.putInt("cid",getData.Id)
                bundle.putString("title",getData.ArabicText)
                gotoFrag(R.id.action_global_podcastCategoryFragment,bundle)
            }
        }

    }

    override fun adClicked(items: com.deenislamic.sdk.service.network.response.advertisement.Data) {
        lifecycleScope.launch {
            async {  userTrackViewModel.saveAdvertisementrecord(items.Id,"redirect") }.await()
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(items.redirecturl))
        if (context?.packageManager?.let { intent.resolveActivity(it) } != null) {
            context?.startActivity(intent)
        }
    }

    override fun patchClicked(data: Item) {
        patchItemClicked(data)
    }

}