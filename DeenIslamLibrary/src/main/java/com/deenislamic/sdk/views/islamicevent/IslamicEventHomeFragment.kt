package com.deenislamic.sdk.views.islamicevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.islamicevent.IslamicEventCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamicEventsResource
import com.deenislamic.sdk.service.network.response.islamicevent.IslamicEventListResponse
import com.deenislamic.sdk.service.repository.IslamicEventRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.MENU_ISLAMIC_EVENT
import com.deenislamic.sdk.utils.get9DigitRandom
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.viewmodels.IslamicEventViewModel
import com.deenislamic.sdk.views.adapters.islamicevents.IslamicEventsHomeAdapter
import com.deenislamic.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch


internal class IslamicEventHomeFragment : BaseRegularFragment(), IslamicEventCallback {

    private lateinit var viewmodel: IslamicEventViewModel
    private lateinit var listIslamicEvents: RecyclerView
    private var firstload = false
    private lateinit var islamiceventhomeadapter:IslamicEventsHomeAdapter

    override fun OnCreate() {
        super.OnCreate()
        setupBackPressCallback(this,true)
        // init viewmodel
        val repository = IslamicEventRepository(NetworkProvider().getInstance().provideDeenService())
        viewmodel = IslamicEventViewModel(repository)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainview = localInflater.inflate(R.layout.fragment_islamic_event_home, container, false)

        listIslamicEvents = mainview.findViewById(R.id.rvEvent)

        setupActionForOtherFragment(
            action1 = 0,
            action2 = 0,
            callback = null,
            actionnBartitle = localContext.getString(R.string.islamic_event),
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
                    pagename = "islamic_event",
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

        loadApi()
    }

    private fun loadApi() {
        baseLoadingState()
        lifecycleScope.launch {
            viewmodel.getIslamicEvents(getLanguage())
        }
    }

    private fun initObserver() {
        viewmodel.islamicEvent.observe(viewLifecycleOwner)
        {
            when (it) {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is IslamicEventsResource.islamicEvents -> {

                    viewState(it.data)
                }
            }
        }
    }

    override fun noInternetRetryClicked() {
        super.noInternetRetryClicked()

        loadApi()
    }

    override fun onBackPress() {
        if(isVisible) {
            lifecycleScope.launch {
                userTrackViewModel.trackUser(
                    language = getLanguage(),
                    msisdn = DeenSDKCore.GetDeenMsisdn(),
                    pagename = "islamic_event",
                    trackingID = getTrackingID()
                )
            }
        }

        tryCatch { super.onBackPress() }

    }

    private fun viewState(data: List<IslamicEventListResponse.Data>) {

        baseViewState()
        listIslamicEvents.apply {
            if(!this@IslamicEventHomeFragment::islamiceventhomeadapter.isInitialized)
                islamiceventhomeadapter = IslamicEventsHomeAdapter(data)
            adapter = islamiceventhomeadapter
        }
    }

    override fun eventCatItemClick(item: IslamicEventListResponse.Data) {

        val bundle = Bundle()
        bundle.putInt("categoryID", item.id)
        bundle.putString("pageTitle",item.category)
        bundle.putString("pageTag",MENU_ISLAMIC_EVENT)
        gotoFrag(R.id.action_global_subContentFragment,bundle)
    }


}