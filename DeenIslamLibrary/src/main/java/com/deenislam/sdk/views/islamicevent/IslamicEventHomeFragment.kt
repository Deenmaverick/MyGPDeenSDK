package com.deenislam.sdk.views.islamicevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.islamicevent.IslamicEventCallback
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.IslamicEventsResource
import com.deenislam.sdk.service.network.response.islamicevent.IslamicEventListResponse
import com.deenislam.sdk.service.repository.IslamicEventRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.MENU_ISLAMIC_EVENT
import com.deenislam.sdk.viewmodels.IslamicEventViewModel
import com.deenislam.sdk.views.adapters.islamicevents.IslamicEventsHomeAdapter
import com.deenislam.sdk.views.base.BaseRegularFragment
import kotlinx.coroutines.launch


internal class IslamicEventHomeFragment : BaseRegularFragment(), IslamicEventCallback {

    private lateinit var viewmodel: IslamicEventViewModel
    private lateinit var listIslamicEvents: RecyclerView
    private var firstload = false

    override fun OnCreate() {
        super.OnCreate()
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

        if(firstload) {
            loadpage()
        }
        else if (!isDetached) {
            view.postDelayed({
                loadpage()
            }, 300)
        }
        else
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

    private fun viewState(data: List<IslamicEventListResponse.Data>) {

        baseViewState()
        listIslamicEvents.apply {
            adapter = IslamicEventsHomeAdapter(data)
        }
    }

    override fun eventCatItemClick(item: IslamicEventListResponse.Data) {

        val bundle = Bundle()
        bundle.putInt("categoryID", item.id)
        bundle.putString("pageTitle",item.category)
        bundle.putString("pageTag", MENU_ISLAMIC_EVENT)
        bundle.putBoolean("shareable",true)

        gotoFrag(R.id.action_global_subCatCardListFragment,bundle)
    }


}