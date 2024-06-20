package com.deenislamic.sdk.views.islamicboyan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.BoyanCategoryCardListCallback
import com.deenislamic.sdk.service.callback.common.HorizontalCardListCallback
import com.deenislamic.sdk.service.di.NetworkProvider
import com.deenislamic.sdk.service.models.BoyanResource
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.service.repository.BoyanRepository
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.viewmodels.BoyanViewModel
import com.deenislamic.sdk.views.base.BaseRegularFragment
import com.deenislamic.sdk.views.adapters.islamicboyan.IslamicBoyanHomeAdapter
import kotlinx.coroutines.launch


internal class BoyanHomeFragment : BaseRegularFragment(), HorizontalCardListCallback,
    BoyanCategoryCardListCallback {

    private lateinit var viewmodel: BoyanViewModel

    private lateinit var recyclerViewBoyan: RecyclerView
    private var firstload = false


    override fun OnCreate() {
        super.OnCreate()
        val boyanRepository = BoyanRepository(deenService = NetworkProvider().getInstance().provideDeenService())
        viewmodel = BoyanViewModel(boyanRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = localInflater.inflate(R.layout.fragment_boyan_home, container, false)


        //init view
        recyclerViewBoyan = mainView.findViewById(R.id.listMainBoyan)

        setupCommonLayout(mainView)
        CallBackProvider.setFragment(this)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()
        baseLoadingState()

        if(!firstload)
            loadApi()
        firstload = true
    }


    private fun initObserver() {
        viewmodel.boyanLiveData.observe(viewLifecycleOwner)
        {
            when (it) {
                is CommonResource.API_CALL_FAILED -> baseNoInternetState()
                is CommonResource.EMPTY -> baseEmptyState()
                is BoyanResource.BoyanHomeData -> {
                    //Toast.makeText(requireContext(), "data: "+it.data, Toast.LENGTH_SHORT).show()
                    recyclerViewBoyan.apply {
                        isNestedScrollingEnabled = false
                        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        adapter = IslamicBoyanHomeAdapter(it.data)
                    }
                    baseViewState()
                }
            }
        }
    }

    private fun loadApi() {
        lifecycleScope.launch {
            viewmodel.getBoyanHome(getLanguage())
        }
    }

    override fun smallCardPatchItemClicked(getData: Item) {
        //Toast.makeText(requireContext(), "data:"+getData.Text, Toast.LENGTH_SHORT).show()
        val bundle = Bundle()
        bundle.putInt("id", getData.Id)
        bundle.putString("videoType", "scholar")
        bundle.putString("title",getData.Text)
        gotoFrag(R.id.action_global_boyanVideoPreviewFragment, bundle)
    }

    override fun boyanCaregoryCardPatchItemClicked(getData: Item) {
        val bundle = Bundle()
        bundle.putInt("id", getData.Id)
        bundle.putString("videoType", "category")
        bundle.putString("title",getData.Text)
        gotoFrag(R.id.action_global_boyanVideoPreviewFragment, bundle)
    }

    override fun patchItemClicked(getData: Item) {
        val bundle = Bundle()
        bundle.putInt("id", getData.CategoryId)
        bundle.putString("videoType", "category")
        bundle.putString("videoUrl", getData.Reference)
        bundle.putString("videoName", getData.Title)
        gotoFrag(R.id.action_global_boyanVideoFragment, bundle)
    }

    override fun noInternetRetryClicked() {
        loadApi()
    }


    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible){
            CallBackProvider.setFragment(this)
        }
    }
}