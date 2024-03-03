package com.deenislam.sdk.views.duaandamal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.IslamicEducationCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.IslamicEducationVideoResource
import com.deenislam.sdk.service.network.response.common.CommonCardData
import com.deenislam.sdk.service.network.response.islamiceducationvideo.Data
import com.deenislam.sdk.service.repository.IslamicEducationVideoRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.get9DigitRandom
import com.deenislam.sdk.utils.tryCatch
import com.deenislam.sdk.viewmodels.IslamicEducationViewModel
import com.deenislam.sdk.views.adapters.duaamal.DualAmolHomeAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch


internal class DuaAmalHomeFragment : BaseRegularFragment(), IslamicEducationCallback{

    private lateinit var listMainEducationVideo: RecyclerView
    private lateinit var viewmodel: IslamicEducationViewModel
    private var firstload = false
    var videoList: List<CommonCardData> = ArrayList()
    private val navArgs:DuaAmalHomeFragmentArgs by navArgs()


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
            actionnBartitle = localContext.getString(R.string.dua_amol),
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
                    pagename = "duaamol",
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

        if(!firstload) {
            baseLoadingState()

            loadApi()
        }
        firstload = true
    }


    private fun initObserver() {
        viewmodel.educationVideo.observe(viewLifecycleOwner)
        {
            when (it) {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is IslamicEducationVideoResource.educationVideo -> {
                    videoList = it.data.Content
                    viewState(it.data)
                }
            }
        }
    }

    private fun loadApi() {
        lifecycleScope.launch {
            viewmodel.getDuaAmolHome(getLanguage(),navArgs.date)
        }
    }

    private fun viewState(data: Data) {

        listMainEducationVideo.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = DualAmolHomeAdapter(arrayListOf(),data.Content)
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
                    pagename = "duaamol",
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
        gotoFrag(R.id.action_global_duaAmalVideoPlayerFragment, bundle)
    }


}