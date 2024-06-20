package com.deenislamic.sdk.views.islamiceducationvideo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.IslamicEducationCallback
import com.deenislamic.sdk.service.callback.common.CommonCardCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamicEducationVideoResource
import com.deenislamic.sdk.service.network.response.islamiceducationvideo.Data
import com.deenislamic.sdk.service.repository.IslamicEducationVideoRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.viewmodels.IslamicEducationViewModel
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.service.network.response.common.CommonCardData
import com.deenislamic.sdk.utils.get9DigitRandom
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.views.adapters.islamiceducationvideo.IslamicEducationVideoHomeAdapter
import kotlinx.coroutines.launch


internal class IslamicEducationVideoHomeFragment : BaseRegularFragment(), IslamicEducationCallback,
    CommonCardCallback {

    private lateinit var listMainEducationVideo: RecyclerView
    private lateinit var viewmodel: IslamicEducationViewModel
    private var firstload = false
    var recentList: List<CommonCardData> = ArrayList()
    var videoList: List<CommonCardData> = ArrayList()


    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)
        // init viewmodel
        val repository = IslamicEducationVideoRepository(NetworkProvider().getInstance().provideDeenService())
        viewmodel = IslamicEducationViewModel(repository)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainview =
            localInflater.inflate(R.layout.fragment_islamic_education_video_home, container, false)

        //init view


        listMainEducationVideo = mainview.findViewById(R.id.listMainEducationVideo)


        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.islamic_education_video),
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
                    pagename = "islamic_educational_video",
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

        firstload = true


    }

    private fun loadpage()
    {
        initObserver()
        baseLoadingState()

        loadApi()
    }


    private fun initObserver() {
        viewmodel.educationVideo.observe(viewLifecycleOwner)
        {
            when (it) {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is IslamicEducationVideoResource.educationVideo -> {
                    recentList = it.data.Recent
                    videoList = it.data.Content
                    viewState(it.data)
                }
            }
        }
    }

    private fun loadApi() {
        lifecycleScope.launch {
            viewmodel.getIslamicEducationVideo(getLanguage())
        }
    }

    private fun viewState(data: Data) {

        listMainEducationVideo.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = IslamicEducationVideoHomeAdapter(data.Recent as ArrayList<CommonCardData>, data.Content)
        }
        baseViewState()
    }

    override fun noInternetRetryClicked() {
        loadApi()
    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "islamic_educational_video",
                    trackingID = getTrackingID()
                )
            }
        }

        tryCatch { super.onBackPress() }

    }

    override fun videoItemClick(position: Int) {

        val bundle = Bundle()
        bundle.putInt("videoPosition", position)
        bundle.putParcelableArray("videoList", videoList.toTypedArray())
        gotoFrag(R.id.action_global_educationVideoFragment, bundle)
    }

    override fun commonCardClicked(getData: CommonCardData, absoluteAdapterPosition: Int) {
        val bundle = Bundle()
        bundle.putInt("videoPosition", absoluteAdapterPosition)
        bundle.putParcelableArray("videoList", recentList.toTypedArray())
        gotoFrag(R.id.action_global_educationVideoFragment, bundle)
    }


}