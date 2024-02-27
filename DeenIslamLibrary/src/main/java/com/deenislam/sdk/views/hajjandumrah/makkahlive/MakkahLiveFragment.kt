package com.deenislam.sdk.views.hajjandumrah.makkahlive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.common.CommonCardCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.HajjAndUmrahResource
import com.deenislam.sdk.service.network.response.common.CommonCardData
import com.deenislam.sdk.service.network.response.hajjandumrah.makkahlive.Data
import com.deenislam.sdk.service.repository.HajjAndUmrahRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.get9DigitRandom
import com.deenislam.sdk.utils.tryCatch
import com.deenislam.sdk.viewmodels.HajjAndUmrahViewModel
import com.deenislam.sdk.views.adapters.common.CommonCardAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch


internal class MakkahLiveFragment : BaseRegularFragment(), CommonCardCallback {

    private lateinit var viewmodel: HajjAndUmrahViewModel
    private var firstload = false
    private lateinit var listview:RecyclerView

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)

        val hajjAndUmrahRepository = HajjAndUmrahRepository(
            deenService = NetworkProvider().getInstance().provideDeenService()
        )

        viewmodel = HajjAndUmrahViewModel(repository = hajjAndUmrahRepository)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_makkah_live,container,false)

        listview = mainview.findViewById(R.id.listview)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.live_makkah_madina),
            backEnable = true,
            view = mainview
        )
        CallBackProvider.setFragment(this)

        setupCommonLayout(mainview)

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
                    pagename = "live_makka_madina",
                    trackingID = getTrackingID()
                )
            }
        }

        /*if(firstload) {
            loadpage()
        }
        else if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }
        else*/
            loadpage()

    }

    private fun loadpage()
    {
        initObserver()

        if(!firstload)
            loadApi()

        firstload = true
    }

    private fun initObserver()
    {
        viewmodel.liveVideosLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is HajjAndUmrahResource.makkahLiveVideos -> viewState(it.data)
            }
        }

    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "live_makka_madina",
                    trackingID = getTrackingID()
                )
            }
        }

        tryCatch { super.onBackPress() }

    }


    private fun loadApi()
    {
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getLiveVideos(getLanguage())
        }
    }

    override fun noInternetRetryClicked() {

        loadApi()
    }

    private fun viewState(data: List<Data>)
    {
        listview.apply {
            adapter = CommonCardAdapter(
                data = ArrayList(data.map { transformNameData(it) }),
                isShowLiveIcon = true,
                isShowPlayIcon = true,
                itemMarginTop = 8.dp,
                itemMarginLeft = 0,
                itemMarginRight = 0
            )
            layoutManager = LinearLayoutManager(this.context,LinearLayoutManager.VERTICAL,false)
        }

        baseViewState()
    }

    private fun transformNameData(newDataModel: Data?): CommonCardData {

        return CommonCardData(
            imageurl = newDataModel?.ImageUrl,
            buttonTxt = newDataModel?.Title,
            videourl = newDataModel?.Reference
        )
    }

    override fun commonCardClicked(getData: CommonCardData, absoluteAdapterPosition: Int) {
        val bundle = Bundle()
        bundle.putString("videoid", getData.videourl)
        bundle.putString("title",getData.buttonTxt)
        gotoFrag(R.id.action_global_ijtemaLiveFragment,bundle)
    }

}