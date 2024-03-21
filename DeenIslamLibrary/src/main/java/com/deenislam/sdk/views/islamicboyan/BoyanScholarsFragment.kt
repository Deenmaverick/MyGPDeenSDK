package com.deenislam.sdk.views.islamicboyan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.di.NetworkProvider
import com.deenislam.sdk.service.models.BoyanResource
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.network.response.boyan.scholarspaging.Data
import com.deenislam.sdk.service.repository.BoyanRepository
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.viewmodels.BoyanViewModel
import com.deenislam.sdk.views.base.BaseRegularFragment
import com.deenislam.sdk.views.islamicboyan.adapter.BoyanScholarCallback
import com.deenislam.sdk.views.islamicboyan.adapter.BoyanScholarsPagindAdapter
import kotlinx.coroutines.launch


internal class BoyanScholarsFragment : BaseRegularFragment(), BoyanScholarCallback {

    private lateinit var listView: RecyclerView
    private lateinit var boyanScholarsPagingAdapter: BoyanScholarsPagindAdapter

    private lateinit var viewModel: BoyanViewModel

    private var firstload: Boolean = false

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
        val mainView = localInflater.inflate(R.layout.fragment_boyan_scholars, container, false)

        //init view
        listView = mainView.findViewById(R.id.listView)

        setupCommonLayout(mainView)

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!this::boyanScholarsPagingAdapter.isInitialized)
            boyanScholarsPagingAdapter = BoyanScholarsPagindAdapter(this@BoyanScholarsFragment, "boyan")

        listView.apply {
            adapter = boyanScholarsPagingAdapter
        }

        initObserver()
    }

    private fun loadApiData()
    {
        lifecycleScope.launch {
            viewModel.getBoyanScholars(language = getLanguage(), page = 1, limit = 100)
        }
    }

    private fun initObserver() {
        viewModel.boyanScholarsLiveData.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is BoyanResource.BoyanScholarData -> {


                    listView.apply {
                        adapter = boyanScholarsPagingAdapter
                    }

                    boyanScholarsPagingAdapter.update(it.data)

                    baseViewState()
                }

            }
        }
    }

    override fun scholarClick(data: Data) {
        val bundle = Bundle()
        bundle.putInt("id", data.Id)
        bundle.putString("videoType", "scholar")
        bundle.putString("title", data.Name)
        gotoFrag(R.id.action_global_boyanVideoPreviewFragment, bundle)
    }

    override fun noInternetRetryClicked() {
        loadApiData()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible){
            CallBackProvider.setFragment(this)
            if(!firstload) {
                loadApiData()
            }
            firstload = true
        }
    }
}